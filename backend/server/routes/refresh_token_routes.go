package routes

import (
	"github.com/VoAnKhoi2005/ReSell/controllers"
	"github.com/VoAnKhoi2005/ReSell/repositories"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterRefreshTokenRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	userRepo := repositories.NewUserRepository(db)
	controller := controllers.NewRefreshTokenController(userRepo)
	rg.POST("/refresh", controller.RefreshToken)
}
