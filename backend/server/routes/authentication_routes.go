package routes

import (
	"github.com/VoAnKhoi2005/ReSell/controllers"
	"github.com/gin-gonic/gin"
)

func RegisterAuthenticationRoutes(rg *gin.RouterGroup, controller *controllers.UserController) {
	rg.POST("/register", controller.Register)
	rg.POST("/login", controller.Login)
}
