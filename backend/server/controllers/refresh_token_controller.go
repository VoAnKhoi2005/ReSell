package controllers

import (
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repositories"
	"github.com/gin-gonic/gin"
	"net/http"
	"os"
	"strconv"
)

type RefreshTokenController struct {
	UserRepository repositories.UserRepository
}

func NewRefreshTokenController(userRepository repositories.UserRepository) *RefreshTokenController {
	return &RefreshTokenController{UserRepository: userRepository}
}

func (rtc *RefreshTokenController) RefreshToken(c *gin.Context) {
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

	id, err := middleware.ExtractIDFromToken(request.RefreshToken, RefreshTokenSecret)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	user, err := rtc.UserRepository.GetByID(id)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	accessToken, err := middleware.CreateAccessToken(user, AccessTokenExpiryHour)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	refreshToken, err := middleware.CreateRefreshToken(user, RefreshTokenExpiryHour)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"accessToken": accessToken, "refreshToken": refreshToken})
}
