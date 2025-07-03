package controller

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
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
		Username:        req.Username,
		Email:           &req.Email,
		IsEmailVerified: false,
		Phone:           &req.Phone,
		IsPhoneVerified: false,
		Password:        encryptedPasswordStr,
		AuthProvider:    model.LocalAuth,
		Reputation:      100,
		Status:          model.ActiveStatus,
		BanStart:        nil,
		BanEnd:          nil,
		CreatedAt:       time.Now().UTC(),
	}

	myErrors := h.userService.Register(&user)
	if len(myErrors) > 0 {
		c.JSON(http.StatusBadRequest, gin.H{"errors": myErrors})
		return
	}

	c.JSON(http.StatusOK, true)
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

	var emailStr, phoneStr string
	var email, phone *string

	uid := token.UID

	if val, ok := token.Claims["email"].(string); ok && val != "" {
		emailStr = val
		email = &emailStr
	}

	if val, ok := token.Claims["phone_number"].(string); ok && val != "" {
		phoneStr = val
		phone = &phoneStr
	}

	var provider model.AuthProviderType
	if email != nil {
		provider = model.GoogleAuth
		phone = nil
	} else if phone != nil {
		provider = model.PhoneAuth
		email = nil
	} else {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid auth provider token"})
		return
	}

	user, err := h.userService.GetUserByFirebaseUID(uid)
	if err != nil {
		if !errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		} else {
			user = nil
		}
	}

	var authResponse transaction.FirebaseAuthResponse

	if user == nil {
		if request.Username == nil || *request.Username == "" ||
			request.Password == nil || *request.Password == "" ||
			request.Fullname == nil || *request.Fullname == "" {
			authResponse = transaction.FirebaseAuthResponse{
				FirstTimeLogin: true,
			}

			c.JSON(http.StatusCreated, authResponse)
			return
		}

		encryptedPassword, err := bcrypt.GenerateFromPassword(
			[]byte(*request.Password),
			bcrypt.DefaultCost,
		)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		encryptedPasswordStr := string(encryptedPassword)

		user = &model.User{
			FirebaseUID:  &uid,
			Email:        email,
			Phone:        phone,
			Username:     *request.Username,
			Fullname:     *request.Fullname,
			Password:     encryptedPasswordStr,
			AuthProvider: provider,
			Reputation:   100,
			Status:       model.ActiveStatus,
			CreatedAt:    time.Now().UTC(),
		}

		myErrors := h.userService.Register(user)
		if len(myErrors) > 0 {
			c.JSON(http.StatusBadRequest, gin.H{"errors": myErrors})
			return
		}

		user, err = h.userService.GetUserByFirebaseUID(uid)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
			return
		}
	}

	if user.BanEnd != nil && user.BanEnd.After(time.Now().UTC()) {
		c.JSON(http.StatusForbidden, gin.H{"error": "User is banned"})
		return
	}

	accessToken, refreshToken, err := util.GenerateToken(user.ID, "user")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	authToken := &transaction.TokenResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	}

	authResponse = transaction.FirebaseAuthResponse{
		User:           user,
		Token:          authToken,
		FirstTimeLogin: false,
	}

	c.JSON(http.StatusOK, authResponse)
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

	if user.BanEnd != nil && user.BanEnd.After(time.Now().UTC()) {
		c.JSON(http.StatusForbidden, gin.H{"error": "User is banned"})
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

func (h *AuthController) VerifyToken(c *gin.Context) {
	userIDRaw, ok := c.Get("x-user-id")
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
		return
	}

	userID, ok := userIDRaw.(string)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid user ID"})
		return
	}

	user, err := h.userService.GetUserByID(userID)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User not found"})
		return
	}

	accessToken, refreshToken, err := util.GenerateToken(user.ID, "user")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	authToken := transaction.TokenResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	}

	response := transaction.VerifyTokenResponse{
		User:  *user,
		Token: authToken,
	}

	c.JSON(http.StatusOK, response)
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
