package controllers

import (
	request "github.com/VoAnKhoi2005/ReSell/requests"
	"github.com/VoAnKhoi2005/ReSell/services"
	"github.com/gin-gonic/gin"
	"net/http"
)

type CategoryController struct {
	service services.CategoryService
}

func NewCategoryController(service services.CategoryService) *CategoryController {
	return &CategoryController{service: service}
}

func (h *CategoryController) CreateCategory(c *gin.Context) {
	var req request.CreateCategoryRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	category, err := h.service.CreateCategory(&req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": category.ID, "message": "Category created"})
}

func (h *CategoryController) UpdateCategory(c *gin.Context) {
	var req request.UpdateCategoryRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

}
