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
	repo := repository.NewUserRepository(db)
	userService := service.NewUserService(repo)
	userController := controller.NewUserController(userService)

	//Create users group -> /api/users/...
	users := rg.Group("/users")

	//Middleware
	users.Use(middleware.UserAuthMiddleware())

	//Add paths to group
	users.DELETE("/:id", userController.DeleteUser)
}
