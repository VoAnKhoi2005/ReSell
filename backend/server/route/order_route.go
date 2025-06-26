package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterOrderRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	orderRepository := repository.NewOrderRepository(db)
	postRepo := repository.NewPostRepository(db)
	addressRepo := repository.NewAddressRepository(db)
	orderService := service.NewOrderService(orderRepository, postRepo, addressRepo)
	orderController := controller.NewOrderController(orderService)

	orderRoute := rg.Group("/order")

	orderRoute.POST("/payment/callback", orderController.HandleZaloPayCallback)

	orderRoute.Use(middleware.AuthMiddleware())
	orderRoute.POST("/create", orderController.CreateOrder)
	orderRoute.POST("/:id/pay", orderController.CreateZaloPayPayment)

	orderRoute.DELETE("/:order_id", orderController.DeleteOrder)
	orderRoute.PUT("/:order_id/set_status/:new_status", orderController.UpdateStatus)

	orderRoute.GET("/post/:post_id", orderController.GetByPostID)
	orderRoute.GET("/buyer/:buyer_id", orderController.GetByBuyerID)
	orderRoute.GET("/seller/:seller_id", orderController.GetBySellerID)
	orderRoute.GET("/:order_id", orderController.GetOrderByID)

}
