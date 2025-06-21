package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterPaymentMethodRoutes(rg *gin.RouterGroup, db *gorm.DB) {

	paymentMethodRepo := repository.NewPaymentMethodRepository(db)
	paymentMethodService := service.NewPaymentMethodService(paymentMethodRepo)
	paymentMethodController := controller.NewPaymentMethodController(paymentMethodService)

	paymentMethods := rg.Group("/payment-methods")
	//Both admin and user can access
	paymentMethods.Use(middleware.AuthMiddleware())
	paymentMethods.GET("", paymentMethodController.GetAllPaymentMethods)
	paymentMethods.GET("/:id", paymentMethodController.GetPaymentMethodByID) // Get children paymentMethods by parent ID

	//Only admin
	admin := rg.Group("/admin/payment-methods")
	admin.Use(middleware.AdminAuthMiddleware())
	admin.POST("", paymentMethodController.CreatePaymentMethod)
	admin.PUT("/:id", paymentMethodController.UpdatePaymentMethod)
	admin.DELETE("/:id", paymentMethodController.DeletePaymentMethod)
}
