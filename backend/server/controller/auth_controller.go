package controller

import (
	"github.com/VoAnKhoi2005/ReSell/middlewares"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"net/http"
	"os"
	"strconv"
)

type AuthController struct {
	service service.UserService
}

func NewAuthController(service service.UserService) *AuthController {
	return &AuthController{service: service}
}

func (h *AuthController) Register(c *gin.Context) {
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

func (h *AuthController) Login(c *gin.Context) {

}

func (h *AuthController) RefreshToken(c *gin.Context) {
	var request struct {
		RefreshToken string `json:"refreshToken" binding:"required"`
	}

	err := c.ShouldBind(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	RefreshTokenSecret := os.Getenv("REFRESH_TOKEN_SECRET")
	AccessTokenExpiryHour, err := strconv.Atoi(os.Getenv("ACCESS_TOKEN_EXPIRY_HOUR"))
	RefreshTokenExpiryHour, err := strconv.Atoi(os.Getenv("REFRESH_TOKEN_EXPIRY_HOUR"))

	id, err := middlewares.ExtractIDFromToken(request.RefreshToken, RefreshTokenSecret)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	user, err := h.service.GetUserByID(id)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	accessToken, err := middlewares.CreateAccessToken(user, AccessTokenExpiryHour)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	refreshToken, err := middlewares.CreateRefreshToken(user, RefreshTokenExpiryHour)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"accessToken": accessToken, "refreshToken": refreshToken})
}
