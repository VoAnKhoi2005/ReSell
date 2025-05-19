package routes

import (
	"github.com/gin-gonic/gin"
)

// SetupRoutes registers all API routes
func SetupRoutes(router *gin.Engine) {
	router.GET("/ping", func(c *gin.Context) {
		c.JSON(200, gin.H{"message": "pong"})
	})

	RegisterImageRoutes(router)
}
