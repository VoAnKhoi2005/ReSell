package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"net/http"
	"time"
)

type AdminController struct {
	adminService service.AdminService
}

func NewAdminController(adminService service.AdminService) *AdminController {
	return &AdminController{adminService}
}

func (a *AdminController) RegisterAdmin(c *gin.Context) {
	var request transaction.RegisterAdminRequest
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	encryptedPassword, err := bcrypt.GenerateFromPassword(
		[]byte(request.Password),
		bcrypt.DefaultCost,
	)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	admin := model.Admin{
		Username:  request.Username,
		Email:     request.Email,
		Password:  string(encryptedPassword),
		CreatedAt: time.Now(),
	}

	errors := a.adminService.Register(&admin)

	if errors != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": errors})
		return
	}
	c.JSON(http.StatusOK, true)
}

func (a *AdminController) GetAdminByID(c *gin.Context) {
	adminID := c.Param("admin_id")
	if adminID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "admin_id is required"})
		return
	}

	admin, err := a.adminService.GetByID(adminID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, admin)
}

func (a *AdminController) GetAdminByUsername(c *gin.Context) {
	username := c.Param("username")
	if username == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "admin_id is required"})
		return
	}

	admin, err := a.adminService.GetByUsername(username)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, admin)
}

func (a *AdminController) ChangeEmail(c *gin.Context) {
	newEmail := c.Param("new_email")
	if newEmail == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "no new email provided"})
		return
	}

	adminID, err := util.GetAdminID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err = a.adminService.ChangeAdminEmail(adminID, newEmail)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, true)
}

func (a *AdminController) ChangePassword(c *gin.Context) {
	var request transaction.ChangePasswordRequest
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	adminID, err := util.GetAdminID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err = a.adminService.ChangeAdminPassword(adminID, request.OldPassword, request.NewPassword)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, true)
}

func (a *AdminController) DeleteAdmin(c *gin.Context) {
	requestedByAdminID := c.Param("admin_id")

	adminID, err := util.GetAdminID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err = a.adminService.DeleteAdmin(requestedByAdminID, adminID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, true)
}
