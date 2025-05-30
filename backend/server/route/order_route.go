package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterOrderRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	orderRepository := repository.NewOrderRepository(db)
	postRepository := repository.NewPostRepository(db)
	orderService := service.NewOrderService(orderRepository, postRepository)
	orderController := controller.NewOrderController(orderService)

	orderRoute := rg.Group("/order")
	orderRoute.Use(middleware.AuthMiddleware())
	orderRoute.POST("/create", orderController.CreateOrder)
	orderRoute.DELETE("/:order_id", orderController.DeleteOrder)
	orderRoute.PUT("/:order_id/set_status/:new_status", orderController.UpdateStatus)

	orderRoute.GET("/:order_id", orderController.GetOrderByID)
	orderRoute.GET("/post/:post_id", orderController.GetOrderByID)
	orderRoute.GET("/buyer/:buyer_id", orderController.GetByBuyerID)
	orderRoute.GET("/seller/:seller_id", orderController.GetBySellerID)

	orderRoute.POST("/review", orderController.CreateReview)
	orderRoute.DELETE("/review/:review_id", orderController.DeleteReview)
}
