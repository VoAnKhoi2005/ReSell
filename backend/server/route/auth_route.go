package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterAuthRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	userRepo := repository.NewUserRepository(db)
	userService := service.NewUserService(userRepo)

	adminRepo := repository.NewAdminRepository(db)
	adminService := service.NewAdminService(adminRepo)

	authController := controller.NewAuthController(userService, adminService)

	//Create auth group -> /api/auth/...
	auth := rg.Group("/auth")
	auth.POST("/register", authController.Register)
	auth.POST("/login", authController.Login)
	auth.POST("/refresh", authController.RefreshToken)

	admin := auth.Group("/admin")
	admin.POST("/login", authController.LoginAdmin)
	admin.POST("/register", authController.RegisterAdmin)
	admin.POST("/refresh", authController.RefreshAdminToken)
}
