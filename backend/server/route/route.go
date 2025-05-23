package route

import (
	"github.com/VoAnKhoi2005/ReSell/config"
	"github.com/gin-gonic/gin"
	"net/http"
)

// SetupRoutes registers all API route
func SetupRoutes(router *gin.Engine) {
	router.GET("/ping", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{"message": "pong"})
	})

	db := config.DB

	// Group API -> ../api/..
	api := router.Group("/api")
	RegisterAuthRoutes(api, db)
	RegisterUserRoutes(api, db)
	RegisterCategoryRoutes(api, db)
	RegisterImageRoutes(api)

}
