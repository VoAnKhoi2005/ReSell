package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
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
	adminRouter.GET("/id/:admin_id", adminController.GetAdminByID)
	adminRouter.GET("/username/:username", adminController.GetAdminByUsername)
	adminRouter.PUT("/change_password", adminController.ChangePassword)
	adminRouter.PUT("/change_email/:new_email", adminController.ChangeEmail)
	adminRouter.DELETE("/:admin_id", adminController.DeleteAdmin)
}
