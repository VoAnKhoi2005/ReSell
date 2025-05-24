package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterPostRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	posts := rg.Group("/posts")

	postRepo := repository.NewPostRepository(db)
	postService := service.NewPostService(postRepo)
	postController := controller.NewPostController(postService)

	//posts.Use(middleware.JwtAuthMiddleware())

	posts.GET("/", postController.GetAllPosts)
	posts.GET("/:id", postController.GetPostByID)
	posts.POST("/", postController.CreatePost)
	posts.PUT("/:id", postController.UpdatePost)
	posts.DELETE("/:id", postController.DeletePost)
}
