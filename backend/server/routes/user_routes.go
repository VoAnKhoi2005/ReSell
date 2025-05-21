package routes

import (
	"github.com/VoAnKhoi2005/ReSell/middlewares"
	"github.com/VoAnKhoi2005/ReSell/repositories"
	"github.com/VoAnKhoi2005/ReSell/services"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)
import "github.com/VoAnKhoi2005/ReSell/controllers"

func RegisterUserRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	//init repo, service and controller
	repo := repositories.NewUserRepository(db)
	service := services.NewUserService(repo)
	controller := controllers.NewUserController(service)

	//Create users group -> /api/users/...
	users := rg.Group("/users")

	//Middleware
	users.Use(middlewares.JwtAuthMiddleware())

	//Add paths to group
	users.DELETE("/:id", controller.DeleteUser)
}
