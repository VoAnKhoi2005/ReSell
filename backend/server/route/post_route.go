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

	// User & common
	posts := rg.Group("/posts")
	posts.Use(middleware.AuthMiddleware())
	posts.GET("/", postController.GetAllPosts)
	posts.GET("/:id", postController.GetPostByID)
	posts.GET("/trash", postController.GetAllDeletedPosts) // Get all deleted posts
	//posts.GET("/trash/:id", postController.GetDeletedPostByID) // Get a specific deleted post
	posts.GET("/search", postController.SearchPosts)    // Search posts by query
	posts.GET("/filter", postController.GetFiltedPosts) // Filter posts by various criteria
	posts.POST("/", postController.CreatePost)
	posts.PUT("/:id", postController.UpdatePost)
	posts.PUT("/:id/restore", postController.RestoreDeletedPost)
	//posts.PUT("/:id/sold", postController.MarkPostAsSold)
	//posts.PUT("/:id/revert-sold", postController.RevertSoldStatus)
	posts.DELETE("/:id/soft-delete", postController.MarkPostAsDeleted) // soft delete
	posts.DELETE("/:id", postController.DeletePost)                    // hard delete

	// Admin actions
	admin := rg.Group("/admin/posts")
	admin.Use(middleware.AdminAuthMiddleware())
	admin.PUT("/:id/approve", postController.ApprovePost)
	admin.PUT("/:id/reject", postController.RejectPost)
	admin.PUT("/:id/hide", postController.HidePost)
	admin.PUT("/:id/unhide", postController.UnhidePost)

}
