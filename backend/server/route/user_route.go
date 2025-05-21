package route

import (
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)
import "github.com/VoAnKhoi2005/ReSell/controller"

func RegisterUserRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	//init repo, service and controller
	repo := repository.NewUserRepository(db)
	service := service.NewUserService(repo)
	controller := controller.NewUserController(service)

	//Create users group -> /api/users/...
	users := rg.Group("/users")

	//Middleware
	users.Use(middleware.JwtAuthMiddleware())

	//Add paths to group
	users.DELETE("/:id", controller.DeleteUser)
}
