package route

import (
	"github.com/VoAnKhoi2005/ReSell/config"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/middleware/auth"
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

	api.GET("/ws", auth.AuthMiddleware(), middleware.HandleWebSocket)

	RegisterAuthRoutes(api, db)
	RegisterUserRoutes(api, db)
	RegisterAdminRoutes(api, db)
	RegisterMessageRote(api, db)
	RegisterAddressRoutes(api, db)
	RegisterOrderRoutes(api, db)
	RegisterReviewRoute(api, db)
	RegisterCategoryRoutes(api, db)
	RegisterPostRoutes(api, db)
	RegisterCartRoutes(api, db)
}
