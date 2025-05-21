package middlewares

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"os"
	"strings"
)

func JwtAuthMiddleware() gin.HandlerFunc {
	secret := os.Getenv("ACCESS_TOKEN_SECRET")
	return func(c *gin.Context) {
		authHeader := c.Request.Header.Get("Authorization")
		t := strings.Split(authHeader, " ")
		if len(t) == 2 {
			authToken := t[1]
			authorized, err := IsAuthorized(authToken, secret)
			if authorized {
				userID, err := ExtractIDFromToken(authToken, secret)
				if err != nil {
					c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
					c.Abort()
					return
				}
				c.Set("x-user-id", userID)
				c.Next()
				return
			}
			c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
			c.Abort()
			return
		}
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Not authorized"})
		c.Abort()
	}
}
