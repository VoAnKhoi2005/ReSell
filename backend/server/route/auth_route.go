package route

import (
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)
import "github.com/VoAnKhoi2005/ReSell/controller"

func RegisterAuthRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	repo := repository.NewUserRepository(db)
	service := service.NewUserService(repo)
	controller := controller.NewAuthController(service)

	//Create auth group -> /api/auth/...
	auth := rg.Group("/auth")

	//Add path to group
	auth.POST("/register", controller.Register)
	auth.POST("/login", controller.Login)
	auth.POST("/refresh", controller.RefreshToken)
}
