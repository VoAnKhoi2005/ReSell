package config

import (
	"context"
	"fmt"
	"log"

	firebase "firebase.google.com/go"
	"google.golang.org/api/option"
)

func InitFirebase() *firebase.App {
	opt := option.WithCredentialsFile("resell-3afcc-firebase-adminsdk-fbsvc-a0b6d3db87")
	app, err := firebase.NewApp(context.Background(), nil, opt)
	if err != nil {
		log.Fatalf("error initializing Firebase app: %v", err)
	}
	fmt.Println("Firebase app initialized successfully")
	return app
}
