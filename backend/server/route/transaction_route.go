package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterTransactionRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	transactionRepo := repository.NewTransactionRepository(db)
	orderRepo := repository.NewOrderRepository(db)

	transactionService := service.NewTransactionService(transactionRepo, orderRepo)
	transactionController := controller.NewTransactionController(transactionService)

	transactions := rg.Group("/transactions")
	//transactions.Use(middleware.AuthMiddleware())
	transactions.POST("", transactionController.CreateTransaction)
	transactions.POST("/stripe/webhook", transactionController.HandleStripeWebhook)

}
