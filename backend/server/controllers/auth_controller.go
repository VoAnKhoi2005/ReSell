package controllers

import (
	"github.com/VoAnKhoi2005/ReSell/middlewares"
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/services"
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"net/http"
	"os"
	"strconv"
)

type AuthController struct {
	service services.UserService
}

func NewAuthController(service services.UserService) *AuthController {
	return &AuthController{service: service}
}

func (h *AuthController) Register(c *gin.Context) {
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

type LoginRequest struct {
	Identifier string `json:"identifier" binding:"required"`
	Password   string `json:"password" binding:"required"`
	LoginType  string `json:"login_type" binding:"required"` // expects email, phone
}
type LoginResponse struct {
	AccessToken  string `json:"accessToken"`
	RefreshToken string `json:"refreshToken"`
}

func (h *AuthController) Login(c *gin.Context) {
	var request LoginRequest
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	var user *models.User
	var err error
	switch request.LoginType {
	case "email":
		user, err = h.service.GetUserByEmail(request.Identifier)
	case "phone":
		user, err = h.service.GetUserByPhone(request.Identifier)
	default:
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid login type"})
		return
	}

	if err != nil || user == nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid credentials"})
		return
	}

	if bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(request.Password)) != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid credentials"})
		return
	}
}

type RefreshTokenRequest struct {
	RefreshToken string `json:"refreshToken" binding:"required"`
}
type RefreshTokenResponse struct {
	AccessToken  string `json:"accessToken"`
	RefreshToken string `json:"refreshToken"`
}

func (h *AuthController) RefreshToken(c *gin.Context) {
	var request RefreshTokenRequest

	err := c.ShouldBind(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	RefreshTokenSecret := os.Getenv("REFRESH_TOKEN_SECRET")
	AccessTokenExpiryHour, err := strconv.Atoi(os.Getenv("ACCESS_TOKEN_EXPIRY_HOUR"))
	RefreshTokenExpiryHour, err := strconv.Atoi(os.Getenv("REFRESH_TOKEN_EXPIRY_HOUR"))

	userId, err := middlewares.ExtractIDFromToken(request.RefreshToken, RefreshTokenSecret)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	user, err := h.service.GetUserByID(userId)
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

	refreshTokenResponse := RefreshTokenResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	}

	c.JSON(http.StatusOK, refreshTokenResponse)
}
