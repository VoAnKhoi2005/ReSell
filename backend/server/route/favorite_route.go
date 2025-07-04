package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterFavoriteRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	favoriteService := service.NewFavoriteService(repository.NewCartItemRepository(db))
	postService := service.NewPostService(repository.NewPostRepository(db))

	cartController := controller.NewFavoriteController(favoriteService, postService)

	// Create favorite group -> /api/favorite/...
	favorite := rg.Group("/favorite")
	favorite.Use(middleware.AuthMiddleware())

	favorite.GET("", cartController.GetCartItems) // Get all items in the favorite
	favorite.GET(":id", cartController.IsFavorite)
	favorite.POST("", cartController.CreateCartItem)       // Add an item to the favorite
	favorite.DELETE("/:id", cartController.DeleteCartItem) // RemoveSession an item from the carts
}
