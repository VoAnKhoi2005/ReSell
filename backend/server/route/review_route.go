package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterReviewRoute(rg *gin.RouterGroup, db *gorm.DB) {
	reviewRepo := repository.NewReviewRepository(db)
	orderRepo := repository.NewOrderRepository(db)
	reviewService := service.NewReviewService(reviewRepo, orderRepo)
	reviewController := controller.NewReviewController(reviewService)

	reviewRoute := rg.Group("/review")
	reviewRoute.Use(middleware.AuthMiddleware())
	reviewRoute.POST("/create", reviewController.CreateReview)
	reviewRoute.GET("/buyer/:buyer_id", reviewController.GetReviewsByBuyerID)
	reviewRoute.GET("/post/:post_id", reviewController.GetReviewByPostID)
	reviewRoute.GET("/order/:order_id", reviewController.GetReviewByOrderID)
	reviewRoute.DELETE("/order/:order_id", reviewController.DeleteReviewByOrderID)
}
