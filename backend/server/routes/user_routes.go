package routes

import "github.com/gin-gonic/gin"
import "github.com/VoAnKhoi2005/ReSell/controllers"

func RegisterUserRoutes(rg *gin.RouterGroup, controller *controllers.UserController) {
	//Create users group
	users := rg.Group("/users")

	//Add path to group
	users.POST("/register", controller.Register)
	users.POST("/login", controller.Login)
	users.DELETE("/account", controller.DeleteAccount)
}
