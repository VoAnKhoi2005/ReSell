package routes

import (
	"github.com/VoAnKhoi2005/ReSell/repositories"
	"github.com/VoAnKhoi2005/ReSell/services"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)
import "github.com/VoAnKhoi2005/ReSell/controllers"

func RegisterAuthRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	repo := repositories.NewUserRepository(db)
	service := services.NewUserService(repo)
	controller := controllers.NewAuthController(service)

	//Create auth group -> /api/auth/...
	auth := rg.Group("/auth")

	//Add path to group
	auth.POST("/register", controller.Register)
	auth.POST("/login", controller.Login)
	auth.POST("/refresh", controller.RefreshToken)
}
