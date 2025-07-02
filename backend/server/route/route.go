package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/recommender"
	"github.com/gin-gonic/gin"
)

// SetupRoutes registers all API route
func SetupRoutes(router *gin.Engine) {
	router.GET("/ping", func(c *gin.Context) {
		c.JSON(200, gin.H{"message": "pong"})
	})

	db := config.DB

	// Group API -> ../api/..
	api := router.Group("/api")

	RegisterAuthRoutes(api, db)
	RegisterUserRoutes(api, db)
	RegisterAdminRoutes(api, db)
	RegisterMessageRote(api, db)
	RegisterAddressRoutes(api, db)
	RegisterOrderRoutes(api, db)
	RegisterReviewRoute(api, db)
	RegisterCategoryRoutes(api, db)
	RegisterPostRoutes(api, db)
	RegisterFavoriteRoutes(api, db)
	RegisterNotificationRote(api, db)
	RegisterTransactionRoutes(api, db)
	RegisterPaymentMethodRoutes(api, db)
	RegisterCommunityRoutes(api, db)
	RegisterParticipantRoutes(api, db)
	RegisterSubscriptionRoutes(api, db)
	RegisterStripeRoutes(api, db)
	RegisterReportRoutes(api, db)
	recommender.RegisterRecommenderRoute(api, db)
}
