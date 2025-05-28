package route

import (
	"github.com/VoAnKhoi2005/ReSell/config"
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
	RegisterMessageRote(api, db)
	RegisterAddressRoutes(api, db)
	RegisterCategoryRoutes(api, db)
	RegisterPostRoutes(api, db)
}
