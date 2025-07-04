package main

import (
	"context"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/db/data"
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/route"
	"github.com/VoAnKhoi2005/ReSell/backend/server/zalo"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"golang.ngrok.com/ngrok/v2"
	"log"
	"math/rand"
	"os"
	"time"
)

func main() {
	rand.Seed(time.Now().UnixNano())
	config.LoadEnv()
	port := os.Getenv("PORT")
	host := os.Getenv("HOST")

	if host == "" {
		host = "localhost"
	}

	if port == "" {
		port = "8080"
	}

	address := fmt.Sprintf("http://%s:%s", host, port)

	config.ConnectDatabase()
	config.InitRedis()
	config.InitStripe()
	config.AutoMigrate()

	data.GenerateSeedData()
	repository.InitGlobalRepository(config.DB)

	//Firebase
	fb.InitFirebase()
	fb.InitFCMHandler()

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

	go func() {
		if err := r.Run(":" + port); err != nil {
			log.Fatalf("failed to run server: %v", err)
		}
	}()

	if err := run(context.Background(), address); err != nil {
		log.Fatal(err)
	}

}

func run(ctx context.Context, address string) error {
	agent, err := ngrok.NewAgent(ngrok.WithAuthtoken(os.Getenv("NGROK_AUTHTOKEN")))
	if err != nil {
		return err
	}

	ln, err := agent.Forward(ctx,
		ngrok.WithUpstream(address),
	)

	if err != nil {
		fmt.Println("Error", err)
		return err
	}

	fmt.Println("Endpoint online: forwarding from", ln.URL(), "to", address)
	zalo.SetCallbackURL(ln.URL().String())
	// Explicitly stop forwarding; otherwise it runs indefinitely
	<-ln.Done()
	return nil
}
