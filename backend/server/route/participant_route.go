package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterParticipantRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	participantRepo := repository.NewParticipantRepository(db)
	participantService := service.NewParticipantService(participantRepo)
	participantController := controller.NewParticipantController(participantService)

	participants := rg.Group("/communities")
	participants.Use(middleware.AuthMiddleware())
	participants.POST("/:id/join", participantController.JoinCommunity)
	participants.DELETE(":id/leave", participantController.LeaveCommunity)
	participants.PUT(":id/approve", participantController.ApproveParticipant)
	participants.PUT(":/id/reject", participantController.RejectParticipant)
	participants.GET(":/id/participants", participantController.GetParticipants)
}
