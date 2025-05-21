package controller

import (
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
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
	var req model.User
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

func (h *UserController) RefreshToken(c *gin.Context) {
	var request struct {
		RefreshToken string `json:"refreshToken" binding:"required"`
	}

	err := c.ShouldBind(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	id, err := middleware.ExtractIDFromToken(request.RefreshToken)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	user, err := h.service.GetUserByID(id)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	accessToken, err := middleware.CreateAccessToken(user.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	refreshToken, err := middleware.CreateRefreshToken(user.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"accessToken": accessToken, "refreshToken": refreshToken})
}

func (h *UserController) DeleteUser(c *gin.Context) {

}
