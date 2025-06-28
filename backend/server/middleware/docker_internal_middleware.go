package middleware

import (
	"github.com/gin-gonic/gin"
	"net"
	"net/http"
)

func DockerInternalAuth() gin.HandlerFunc {
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
