package routes

import (
	"github.com/VoAnKhoi2005/ReSell/config"
	controller "github.com/VoAnKhoi2005/ReSell/controllers"
	"github.com/VoAnKhoi2005/ReSell/repositories"
	service "github.com/VoAnKhoi2005/ReSell/services"
	"github.com/gin-gonic/gin"
)

// SetupRoutes registers all API routes
func SetupRoutes(router *gin.Engine) {
	router.GET("/ping", func(c *gin.Context) {
		c.JSON(200, gin.H{"message": "pong"})
	})

	db := config.DB

	userRepo := repositories.NewUserRepository(db)
	userService := service.NewUserService(userRepo)
	userController := controller.NewUserController(userService)

	// Group API -> ../api/..
	api := router.Group("/api")

	RegisterUserRoutes(api, userController)
	RegisterImageRoutes(api)

}
