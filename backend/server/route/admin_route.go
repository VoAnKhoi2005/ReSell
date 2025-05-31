package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterAdminRoutes(router *gin.RouterGroup, db *gorm.DB) {
	adminRepo := repository.NewAdminRepository(db)
	adminService := service.NewAdminService(adminRepo)
	adminController := controller.NewAdminController(adminService)

	adminRouter := router.Group("/admin")
	adminRouter.Use(middleware.AdminAuthMiddleware())
	adminRouter.POST("/register", adminController.RegisterAdmin)
	adminRouter.PUT("/change_password", adminController.ChangePassword)
	adminRouter.PUT("/change_email/:new_email", adminController.ChangeEmail)
}
