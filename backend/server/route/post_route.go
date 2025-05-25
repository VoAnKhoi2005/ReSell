package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterPostRoutes(rg *gin.RouterGroup, db *gorm.DB) {

	postRepo := repository.NewPostRepository(db)
	postService := service.NewPostService(postRepo)
	postController := controller.NewPostController(postService)

	//All
	posts := rg.Group("/posts")
	posts.Use(middleware.AuthMiddleware())
	posts.GET("/", postController.GetAllPosts)
	posts.GET("/:id", postController.GetPostByID)
	posts.POST("/", postController.CreatePost)
	posts.PUT("/:id", postController.UpdatePost)
	posts.DELETE("/:id", postController.DeletePost)

	//Admin
	admin := rg.Group("/admin/posts")
	admin.Use(middleware.AdminAuthMiddleware())

}
