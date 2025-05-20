package main

import (
	"github.com/VoAnKhoi2005/ReSell/config"
	"github.com/VoAnKhoi2005/ReSell/routes"
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
	config.RunMigrations()

	router := gin.Default()
	routes.SetupRoutes(router)

	log.Printf("Server is running at http://localhost:%s\n", port)
	err := router.Run(":" + port)
	if err != nil {
		log.Fatalln("Failed to start server: ", err)
	}
}
