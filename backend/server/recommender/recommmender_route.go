package recommender

import (
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
	"net"
	"net/http"
)

func NewRecommenderRoute(db *gorm.DB, rg *gin.RouterGroup) {
	repo := NewRecommenderRepository(db)
	service := NewRecommenderService(repo)
	controller := NewRecommenderController(service)

	recommenderRoute := rg.Group("/recommender")
	recommenderRoute.Use(OnlyAllowDockerInternal())

	recommenderRoute.GET("/profile/:user_id", controller.GetBuyerProfile)
	recommenderRoute.GET("/post")
}

func OnlyAllowDockerInternal() gin.HandlerFunc {
	return func(c *gin.Context) {
		ip := net.ParseIP(c.ClientIP())
		_, subnet, _ := net.ParseCIDR("172.18.0.0/16")
		if !subnet.Contains(ip) && c.ClientIP() != "127.0.0.1" {
			c.AbortWithStatusJSON(http.StatusForbidden, gin.H{"error": "Internal access only"})
			return
		}
		c.Next()
	}
}
