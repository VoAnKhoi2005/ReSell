package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
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
	cart.DELETE("/:id", cartController.DeleteCartItem) // Remove an item from the carts
}
