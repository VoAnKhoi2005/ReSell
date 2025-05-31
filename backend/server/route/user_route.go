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
	userController := controller.NewUserController(userService)

	//CreateMessage users group -> /api/users/...
	users := rg.Group("/user")
	//Middleware
	users.Use(middleware.AuthMiddleware())
	//Add paths to group
	users.GET("/:id", userController.GetUserByID)
	users.PUT("/update", userController.UpdateUser)
	users.PUT("/change_password", userController.ChangePassword)
	users.DELETE("/:id", userController.DeleteUser)

	users.POST("/follow", userController.Follow)

	//admin
	adminRoute := rg.Group("/admin/user")
	adminRoute.Use(middleware.AdminAuthMiddleware())
	adminRoute.PUT("/ban", userController.BanUser)
	adminRoute.PUT("/unban/:id", userController.UnBanUser)
}
