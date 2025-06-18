package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterAuthRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	userRepo := repository.NewUserRepository(db)
	userService := service.NewUserService(userRepo)

	adminRepo := repository.NewAdminRepository(db)
	adminService := service.NewAdminService(adminRepo)

	authController := controller.NewAuthController(userService, adminService)

	//CreateMessage auth group -> /api/auth/...
	auth := rg.Group("/auth")
	//auth.POST("/register", authController.Register)
	auth.POST("/login", authController.Login)
	auth.POST("/refresh", authController.RefreshToken)

	auth.POST("/register/firebase", authController.FirebaseAuth)

	admin := rg.Group("/admin/auth")
	admin.POST("/login", authController.LoginAdmin)
	admin.POST("/refresh", authController.RefreshAdminToken)
}
