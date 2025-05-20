package controllers

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/services"
	"github.com/gin-gonic/gin"
	"net/http"
)

type UserController struct {
	service service.UserService
}

func NewUserController(service service.UserService) *UserController {

	return &UserController{service: service}
}

func (h *UserController) Register(c *gin.Context) {
	//Not done
	var req models.User
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err := h.service.Register(&req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "register failed"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "registered successfully"})
}

func (h *UserController) Login(c *gin.Context) {

}

func (h *UserController) DeleteUser(c *gin.Context) {

}
