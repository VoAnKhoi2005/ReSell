package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"net/http"
	"time"
)

type AuthController struct {
	userService  service.UserService
	adminService service.AdminService
}

func NewAuthController(userService service.UserService, adminService service.AdminService) *AuthController {
	return &AuthController{
		userService:  userService,
		adminService: adminService,
	}
}

func (h *AuthController) Register(c *gin.Context) {
	var req transaction.RegisterRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	encryptedPassword, err := bcrypt.GenerateFromPassword(
		[]byte(req.Password),
		bcrypt.DefaultCost,
	)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	encryptedPasswordStr := string(encryptedPassword)

	user := model.User{
		Username:     req.Username,
		Email:        req.Email,
		Phone:        req.Phone,
		PasswordHash: &encryptedPasswordStr,
		AuthProvider: model.LocalAuth,
		Reputation:   100,
		Status:       model.ActiveStatus,
		BanStart:     nil,
		BanEnd:       nil,
		CreatedAt:    time.Now(),
		UpdatedAt:    nil,
	}

	err = h.userService.Register(&user)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (h *AuthController) FirebaseAuth(c *gin.Context) {
	var request transaction.FirebaseAuthRequest
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	token, err := fb.VerifyFirebaseIDToken(request.FirebaseIDToken)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid Firebase token"})
		return
	}

	uid := token.UID
	email, _ := token.Claims["email"].(string)
	phone, _ := token.Claims["phone_number"].(string)

	var provider model.AuthProviderType
	if email != "" {
		provider = model.GoogleAuth
	} else if phone != "" {
		provider = model.PhoneAuth
	} else {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid auth provider token"})
		return
	}

	user, err := h.userService.GetUserByFirebaseUID(uid)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if user == nil {
		user = &model.User{
			FirebaseUID:  &uid,
			Email:        email,
			Phone:        phone,
			Username:     request.Username,
			AuthProvider: provider,
			Reputation:   100,
			Status:       model.ActiveStatus,
			CreatedAt:    time.Now(),
		}

		err = h.userService.Register(user)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to register"})
			return
		}

		user, err = h.userService.GetUserByFirebaseUID(uid)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
			return
		}
	}

	accessToken, refreshToken, err := util.GenerateToken(user.ID, "user")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	loginResponse := transaction.LoginResponse{
		User: user,
		Token: transaction.TokenResponse{
			AccessToken:  accessToken,
			RefreshToken: refreshToken,
		},
	}

	c.JSON(http.StatusOK, loginResponse)
}

func (h *AuthController) Login(c *gin.Context) {
	var request transaction.LoginRequest
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	user, err := h.userService.Login(request.Identifier, request.Password, request.LoginType)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	accessToken, refreshToken, err := util.GenerateToken(user.ID, "user")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	loginResponse := transaction.LoginResponse{
		User: user,
		Token: transaction.TokenResponse{
			AccessToken:  accessToken,
			RefreshToken: refreshToken,
		},
	}

	c.JSON(http.StatusOK, loginResponse)
}

func (h *AuthController) RefreshToken(c *gin.Context) {
	var request transaction.RefreshTokenRequest

	err := c.ShouldBind(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	ID, err := middleware.ExtractIDFromToken(request.RefreshToken)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	user, err := h.userService.GetUserByID(ID)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	accessToken, refreshToken, err := util.GenerateToken(user.ID, "user")
	refreshTokenResponse := transaction.TokenResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	}

	c.JSON(http.StatusOK, refreshTokenResponse)
}

func (h *AuthController) LoginAdmin(c *gin.Context) {
	var request transaction.LoginAdminRequest

	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
	}

	admin, err := h.adminService.Login(request.Username, request.Password)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	accessToken, refreshToken, err := util.GenerateToken(admin.ID, "admin")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	loginAdminResponse := transaction.LoginAdminResponse{
		Admin: admin,
		Token: transaction.TokenResponse{
			AccessToken:  accessToken,
			RefreshToken: refreshToken,
		},
	}

	c.JSON(http.StatusOK, loginAdminResponse)
}

func (h *AuthController) RefreshAdminToken(c *gin.Context) {
	var request transaction.RefreshTokenRequest

	err := c.ShouldBind(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	ID, err := middleware.ExtractIDFromToken(request.RefreshToken)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	admin, err := h.adminService.GetByID(ID)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	accessToken, refreshToken, err := util.GenerateToken(admin.ID, "admin")
	refreshTokenResponse := transaction.TokenResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	}

	c.JSON(http.StatusOK, refreshTokenResponse)
}
