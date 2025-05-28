package controller

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/service"
	request "github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/VoAnKhoi2005/ReSell/util"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"net/http"
	"strconv"
)

type PostController struct {
	service service.PostService
}

func NewPostController(service service.PostService) *PostController {
	return &PostController{service: service}
}

func (h *PostController) GetAllPosts(c *gin.Context) {

	posts, err := h.service.GetAllPosts()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, posts)

}

func (h *PostController) GetPostByID(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.GetPostByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, post)
}

func (h *PostController) CreatePost(c *gin.Context) {
	var req request.CreatePostRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	userID, _ := util.GetUserID(c)

	if !util.IsUserOwner(c, userID) {
		return
	}

	post, err := h.service.CreatePost(&req, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": post.ID, "message": "Post created"})
}

func (h *PostController) UpdatePost(c *gin.Context) {
	var req request.UpdatePostRequest

	id := c.Param("id")

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	userID, _ := util.GetUserID(c)

	if !util.IsUserOwner(c, userID) {
		return
	}

	post, err := h.service.UpdatePost(id, &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": post.ID, "message": "Post updated"})

}

func (h *PostController) DeletePost(c *gin.Context) {
	id := c.Param("id")
	err := h.service.DeletePost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Post deleted permanently"})
}

func (h *PostController) ApprovePost(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.ApprovePost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post approved"})
}

func (h *PostController) RejectPost(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.RejectPost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post rejected"})

}

func (h *PostController) HidePost(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.HidePost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post hidden"})
}

func (h *PostController) UnhidePost(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.UnhidePost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post unhidden"})
}

func (h *PostController) MarkPostAsDeleted(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.MarkPostAsDeleted(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post marked as deleted"})
}

func (h *PostController) RestoreDeletedPost(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.RestoreDeletedPost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post restored"})
}

func (h *PostController) MarkPostAsSold(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.MarkPostAsSold(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post marked as sold"})

}

func (h *PostController) RevertSoldStatus(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.RevertSoldStatus(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post sold status reverted"})
}

func (h *PostController) GetAllDeletedPosts(c *gin.Context) {
	posts, err := h.service.GetAllDeletedPosts()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, posts)
}

func (h *PostController) GetDeletedPostByID(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.GetDeletedPostByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, post)
}

func (h *PostController) GetFiltedPosts(c *gin.Context) {
	type Filter struct {
		Status     string
		MinPrice   *uint
		MaxPrice   *uint
		ProvinceID string
		DistrictID string
		WardID     string
		UserID     string
		CategoryID string
	}

	var filter Filter

	// Validate status
	if s := c.Query("status"); s != "" {
		validStatus := map[string]bool{
			"approved": true,
			"rejected": true,
			"hidden":   true,
			"pending":  true,
			"sold":     true,
		}
		if !validStatus[s] {
			c.JSON(http.StatusBadRequest, gin.H{"error": "invalid status"})
			return
		}
		filter.Status = s
	}

	// Min/Max price
	if min := c.Query("min_price"); min != "" {
		if val, err := strconv.ParseUint(min, 10, 64); err == nil {
			v := uint(val)
			filter.MinPrice = &v
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": "min_price must be a number"})
			return
		}
	}
	if max := c.Query("max_price"); max != "" {
		if val, err := strconv.ParseUint(max, 10, 64); err == nil {
			v := uint(val)
			filter.MaxPrice = &v
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": "max_price must be a number"})
			return
		}
	}

	// Validate UUID fields
	validateUUID := func(param string, dest *string) bool {
		if val := c.Query(param); val != "" {
			if _, err := uuid.Parse(val); err != nil {
				c.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("%s must be a valid UUID", param)})
				return false
			}
			*dest = val
		}
		return true
	}

	if !validateUUID("province_id", &filter.ProvinceID) {
		return
	}
	if !validateUUID("district_id", &filter.DistrictID) {
		return
	}
	if !validateUUID("ward_id", &filter.WardID) {
		return
	}
	if !validateUUID("user_id", &filter.UserID) {
		return
	}
	if !validateUUID("category_id", &filter.CategoryID) {
		return
	}

	// Chuyển sang map để truyền xuống repo
	filters := map[string]string{}
	if filter.Status != "" {
		filters["status"] = filter.Status
	}
	if filter.MinPrice != nil {
		filters["min_price"] = fmt.Sprint(*filter.MinPrice)
	}
	if filter.MaxPrice != nil {
		filters["max_price"] = fmt.Sprint(*filter.MaxPrice)
	}
	if filter.ProvinceID != "" {
		filters["province_id"] = filter.ProvinceID
	}
	if filter.DistrictID != "" {
		filters["district_id"] = filter.DistrictID
	}
	if filter.WardID != "" {
		filters["ward_id"] = filter.WardID
	}
	if filter.UserID != "" {
		filters["user_id"] = filter.UserID
	}
	if filter.CategoryID != "" {
		filters["category_id"] = filter.CategoryID
	}

	// Gọi service
	posts, err := h.service.GetPostsByFilter(filters)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, posts)
}

func (h *PostController) SearchPosts(c *gin.Context) {
	q := c.Query("q")
	if q == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "missing search keyword"})
		return
	}

	posts, err := h.service.SearchPosts(q)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, posts)
}

func (h *PostController) UploadPostImages(c *gin.Context) {
	postId := c.Param("id")

	form, err := c.MultipartForm()

	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid form data"})
		return
	}

	files := form.File["images"]
	if len(files) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "no images provided"})
		return
	}

	var imagesUrls []string
	for i, fileHeader := range files {
		file, err := fileHeader.Open()
		defer file.Close()
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to open file"})
			return
		}
		url, err := util.UploadToCloudinary(file, fileHeader)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": fmt.Sprintf("failed to upload image: %s", err.Error())})
			return
		}

		imagesUrls = append(imagesUrls, url)
		_, err = h.service.CreatePostImage(postId, url, uint(i+1))
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": fmt.Sprintf("failed to create post image: %s", err.Error())})
			return
		}
	}
	c.JSON(http.StatusOK, gin.H{"image_urls": imagesUrls, "message": "Images uploaded successfully"})
}
