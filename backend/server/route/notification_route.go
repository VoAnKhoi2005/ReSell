package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterNotificationRote(rg *gin.RouterGroup, db *gorm.DB) {
	notificationRepo := repository.NewNotificationRepository(db)
	notificationService := service.NewNotificationService(notificationRepo)
	notificationController := controller.NewNotificationController(notificationService)

	notificationRoute := rg.Group("/notification")
	notificationRoute.Use(middleware.AuthMiddleware())
	notificationRoute.GET("/batch/:batch_size/:page", notificationController.GetNotificationsByBatch)
	notificationRoute.GET("/date/:date", notificationController.GetNotificationsByDate)
	notificationRoute.GET("/type/:type", notificationController.GetNotificationsByType)

	notificationRoute.POST("/FCM", fb.FcmHandler.SaveFCMToken)
	notificationRoute.DELETE("/FCM", fb.FcmHandler.DeleteFCMToken)

	admin := rg.Group("/admin/notification")
	admin.Use(middleware.AdminAuthMiddleware())
	admin.POST("/test-notification/:user_id", notificationController.SendTestNotification)
}
