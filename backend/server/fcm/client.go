package fcm

import (
	"context"
	"firebase.google.com/go/messaging"
	"log"

	firebase "firebase.google.com/go"
	"google.golang.org/api/option"
)

var fcmClient *messaging.Client

func InitFirebase() {
	opt := option.WithCredentialsFile("resell-3afcc-firebase-adminsdk-fbsvc-5d0b6bc88b.json")
	app, err := firebase.NewApp(context.Background(), nil, opt)
	if err != nil {
		log.Fatalf("error initializing Firebase app: %v", err)
	}

	fcmClient, err = app.Messaging(context.Background())
	if err != nil {
		log.Fatalf("error initializing Messaging client: %v", err)
	}
}
