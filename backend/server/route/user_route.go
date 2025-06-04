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
	users.PUT("/update", userController.UpdateUser)
	users.PUT("/change_password", userController.ChangePassword)
	users.DELETE("/:id", userController.DeleteUser)
	users.POST("/follow/:id", userController.Follow)
	users.GET("/follow/all", userController.GetAllFollowee)
	users.DELETE("unfollow/:id", userController.UnFollow)

	//admin
	adminRoute := rg.Group("/admin/user")
	adminRoute.Use(middleware.AdminAuthMiddleware())
	adminRoute.GET("/id/:id", userController.GetUserByID)
	adminRoute.GET("/username/:username", userController.GetUserByUsername)
	adminRoute.GET("/batch", userController.GetAllUserByBatch)
	adminRoute.PUT("/ban", userController.BanUser)
	adminRoute.PUT("/unban/:id", userController.UnBanUser)
}
