package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/websock"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterMessageRote(rg *gin.RouterGroup, db *gorm.DB) {
	messageRepo := repository.NewMessageRepository(db)
	messageService := service.NewMessageService(messageRepo)
	messageController := controller.NewMessageController(messageService)

	wsHandler := websock.NewWSHandler(messageService)
	rg.GET("/ws", middleware.AuthMiddleware(), wsHandler.Handler)

	messageRoute := rg.Group("/conversation")
	messageRoute.Use(middleware.AuthMiddleware())

	messageRoute.POST("/create", messageController.CreateConversation)
	messageRoute.GET("/:id", messageController.GetConversationByID)
	messageRoute.GET("/post/:post_id", messageController.GetConversationByPostID)
	messageRoute.DELETE("/:id", messageController.DeleteConversation)

	//messageRoute.POST("/:id/messages/", messageController.CreateMessage)
	messageRoute.GET("/:id/messages/latest/:amount", messageController.GetLatestMessages)
	messageRoute.GET("/:id/messages/in_range", messageController.GetMessagesInRange)
}
