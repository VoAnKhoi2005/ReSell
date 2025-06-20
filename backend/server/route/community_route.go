package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterCommunityRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	communityRepo := repository.NewCommunityRepository(db)
	communityService := service.NewCommunityService(communityRepo)
	communityController := controller.NewCommunityController(communityService)

	communities := rg.Group("/communities")
	communities.Use(middleware.AuthMiddleware()) // nếu muốn hạn chế quyền admin

	communities.GET("", communityController.GetCommunities)
	communities.GET("/:id", communityController.GetCommunityByID)
	communities.POST("/", communityController.CreateCommunity)
	communities.PUT("/:id", communityController.UpdateCommunity)
	communities.DELETE("/:id", communityController.DeleteCommunity)

	admin := rg.Group("/admin/communities")
	admin.Use(middleware.AdminAuthMiddleware())
	admin.PUT("/:id/approve", communityController.ApproveCommunity)
	admin.PUT("/:id/reject", communityController.RejectCommunity)

}
