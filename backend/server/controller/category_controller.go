package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	request "github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"net/http"
)

type CategoryController struct {
	service service.CategoryService
}

func NewCategoryController(service service.CategoryService) *CategoryController {
	return &CategoryController{service: service}
}

func (h *CategoryController) GetAllCategories(c *gin.Context) {
	categories, err := h.service.GetAllCategories()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, categories)

}

func (h *CategoryController) GetCategoryByID(c *gin.Context) {
	id := c.Param("id")
	category, err := h.service.GetCategoryByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, category)
}

//func (h *CategoryController) CreateCategory(c *gin.Context) {
//	var req request.CreateCategoryRequest
//	if err := c.ShouldBindJSON(&req); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	category, err := h.service.CreateCategory(&req)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusCreated, gin.H{"id": category.ID, "message": "Category created"})
//}
//
//func (h *CategoryController) UpdateCategory(c *gin.Context) {
//	var req request.UpdateCategoryRequest
//
//	id := c.Param("id")
//
//	if err := c.ShouldBindJSON(&req); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	category, err := h.service.UpdateCategory(id, &req)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusCreated, gin.H{"id": category.ID, "message": "Category updated"})
//
//}

func (h *CategoryController) DeleteCategory(c *gin.Context) {
	id := c.Param("id")
	err := h.service.DeleteCategory(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Category deleted"})
}

func (h *CategoryController) GetChildrenCategories(c *gin.Context) {
	parentID := c.Param("id")
	categories, err := h.service.GetCategoriesByParentID(parentID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, categories)
}

//func (h *CategoryController) UploadCategoryImage(c *gin.Context) {
//	categoryID := c.Param("id")
//	file, fileHeader, err := c.Request.FormFile("image")
//	if err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": "Missing image file"})
//		return
//	}
//
//	defer file.Close()
//
//	imageURL, err := util.UploadToCloudinary(file, fileHeader)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": "Upload failed", "details": err.Error()})
//		return
//	}
//
//	_, err = h.service.UpdateCategoryImage(categoryID, imageURL)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"image_url": imageURL, "message": "Image uploaded successfully"})
//}

func (h *CategoryController) CreateCategory(c *gin.Context) {
	name := c.PostForm("name")
	parentID := c.PostForm("parent_id")

	var imageURL string
	fileHeader, err := c.FormFile("image")
	if err == nil {
		file, err := fileHeader.Open()
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "open failed", "details": err.Error()})
			return
		}
		defer file.Close()

		imageURL, err = util.UploadToCloudinary(file, fileHeader)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "upload failed", "details": err.Error()})
			return
		}
	}

	req := &request.CreateCategoryRequest{
		Name:             name,
		ParentCategoryId: parentID,
		ImageURL:         imageURL, // <- thêm vào struct request
	}

	category, err := h.service.CreateCategory(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": category.ID, "message": "Category created", "image_url": imageURL})
}

func (h *CategoryController) UpdateCategory(c *gin.Context) {
	id := c.Param("id")
	name := c.PostForm("name")

	var imageURL string
	file, fileHeader, err := c.Request.FormFile("image")
	if err == nil {
		defer file.Close()
		imageURL, err = util.UploadToCloudinary(file, fileHeader)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Upload failed", "details": err.Error()})
			return
		}
	}

	req := &request.UpdateCategoryRequest{
		Name:     name,
		ImageURL: imageURL, // <- thêm field này trong struct request
	}

	category, err := h.service.UpdateCategory(id, req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"id": category.ID, "message": "Category updated", "image_url": imageURL})
}
