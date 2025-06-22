package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterStripeRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	userRepo := repository.NewUserRepository(db)
	txRepo := repository.NewTransactionRepository(db)
	stripeService := service.NewStripeService(userRepo, txRepo)
	stripeController := controller.NewStripeController(stripeService)

	stripe := rg.Group("/stripe")
	//stripe.Use(middleware.AuthMiddleware()) // nếu cần

	// Đăng ký bán hàng + tạo Stripe Express Account
	stripe.POST("/webhook", stripeController.HandleStripeWebhook)
	stripe.POST("/transfer", stripeController.TransferToSeller)
	stripe.POST("/create-account/:id", stripeController.CreateExpressAccount)

	// Lấy link onboarding
	stripe.GET("/onboarding-success", stripeController.ReturnAfterCreateExpressAccount)
	stripe.GET("/onboarding-failed", stripeController.RefreshAfterCreateExpressAccount)
	stripe.GET("/onboarding-link/:id", stripeController.GetOnboardingLink)
	// Chuyển tiền từ platform sang seller

}
