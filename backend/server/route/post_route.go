package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterPostRoutes(rg *gin.RouterGroup, db *gorm.DB) {

	postRepo := repository.NewPostRepository(db)
	postService := service.NewPostService(postRepo)
	postController := controller.NewPostController(postService)

	// User & common
	posts := rg.Group("/posts")
	posts.Use(middleware.AuthMiddleware())
	posts.GET("", postController.GetUserPosts)
	posts.GET("/own", postController.GetOwnPosts)
	posts.GET("/followed", postController.GetFollowdPosts)
	posts.GET("/trash", postController.GetAllDeletedPosts) // Get all deleted posts
	posts.GET("/:id", postController.GetPostByID)
	posts.POST("", postController.CreatePost)
	posts.PUT("/:id", postController.UpdatePost)
	posts.PUT("/:id/restore", postController.RestoreDeletedPost)
	posts.DELETE("/:id/soft-delete", postController.MarkPostAsDeleted) // soft delete
	posts.DELETE("/:id", postController.DeletePost)                    // hard delete

	// Admin actions
	admin := rg.Group("/admin/posts")
	admin.Use(middleware.AdminAuthMiddleware())
	admin.GET("", postController.GetAdminPosts) // Get all posts for admin
	admin.PUT("/:id/approve", postController.ApprovePost)
	admin.PUT("/:id/reject", postController.RejectPost)

	//images
	images := rg.Group("/posts/:id/images")

	images.POST("/", postController.UploadPostImages)
	images.DELETE("/", postController.DeletePostImages)

}
