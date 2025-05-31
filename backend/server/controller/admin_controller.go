package controller

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/VoAnKhoi2005/ReSell/util"
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
	c.JSON(http.StatusOK, gin.H{"success": true})
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

	c.JSON(http.StatusOK, gin.H{"success": true})
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

	c.JSON(http.StatusOK, gin.H{"success": true})
}
