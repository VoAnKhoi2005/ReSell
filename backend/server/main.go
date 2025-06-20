package main

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/db/data"
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/route"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"log"
	"os"
)

func main() {
	config.LoadEnv()
	port := os.Getenv("PORT")

	if port == "" {
		port = "8080"
	}

	config.ConnectDatabase()
	config.InitRedis()
	config.InitStripe()

	//Firebase
	fb.InitFirebase()

	r := gin.Default()
	allowOrigin := os.Getenv("FRONTEND_URL")
	if allowOrigin == "" {
		allowOrigin = "http://localhost:5173"
	}
	r.Use(cors.New(cors.Config{
		AllowOrigins:     []string{allowOrigin},
		AllowMethods:     []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Authorization"},
		AllowCredentials: true,
	}))

	r.Use(func(c *gin.Context) {
		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}
		c.Next()
	})

	route.SetupRoutes(r)

	data.GenerateSeedData()

	log.Printf("Server is running at http://localhost:%s\n", port)
	err := r.Run(":" + port)
	if err != nil {
		log.Fatalln("Failed to start server: ", err)
	}
}
