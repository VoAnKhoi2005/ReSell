package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterCartRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	cartService := service.NewCartService(repository.NewCartItemRepository(db))
	postService := service.NewPostService(repository.NewPostRepository(db))

	cartController := controller.NewCartController(cartService, postService)

	// Create cart group -> /api/cart/...
	cart := rg.Group("/:post_id/cart")
	cart.Use(middleware.AuthMiddleware())

	cart.GET("", cartController.GetCartItems)          // Get all items in the cart
	cart.POST("", cartController.CreateCartItem)       // Add an item to the cart
	cart.DELETE("/:id", cartController.DeleteCartItem) // RemoveSession an item from the carts
}
