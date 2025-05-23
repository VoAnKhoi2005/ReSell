package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterUserRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	userRepo := repository.NewUserRepository(db)
	userService := service.NewUserService(userRepo)

	addressRepo := repository.NewAddressRepository(db)
	addressService := service.NewAddressService(addressRepo)

	userController := controller.NewUserController(userService, addressService)

	//Create users group -> /api/users/...
	users := rg.Group("/users")
	//Middleware
	users.Use(middleware.UserAuthMiddleware())
	//Add paths to group
	users.GET("/:id", userController.GetUserByID)
	users.PUT("", userController.UpdateUser)
	users.DELETE("/:id", userController.DeleteUser)

	users.POST("/follow", userController.Follow)
	users.POST("/address", userController.AddAddress)

	//admin
	adminRoute := users.Group("/admin")
	adminRoute.Use(middleware.AdminAuthMiddleware())
	adminRoute.PUT("/ban", userController.BanUser)
	adminRoute.PUT("/unban/:id", userController.UnBanUser)
}
