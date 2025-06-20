package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterSubscriptionRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	subscriptionRepo := repository.NewSubscriptionRepository(db)
	subscriptionService := service.NewSubscriptionService(subscriptionRepo)
	subscriptionController := controller.NewSubscriptionController(subscriptionService)

	plan := rg.Group("/subscriptions/plans")
	plan.Use(middleware.AuthMiddleware())
	plan.GET("/", subscriptionController.GetAllPlans)
	plan.GET("/:id", subscriptionController.GetPlanByID)

	admin := rg.Group("/admin/subscriptions/plans")
	admin.Use(middleware.AdminAuthMiddleware()) // hoặc AdminMiddleware nếu phân quyền
	admin.POST("/", subscriptionController.CreatePlan)
	admin.PUT("/:id", subscriptionController.UpdatePlan)
	admin.DELETE("/:id", subscriptionController.DeletePlan)

}
