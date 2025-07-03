package fb

import (
	"context"
	"firebase.google.com/go/auth"
	"firebase.google.com/go/messaging"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"log"

	firebase "firebase.google.com/go"
	"google.golang.org/api/option"
)

var fcmClient *messaging.Client
var firebaseAuthClient *auth.Client
var FcmHandler *FCMHandler

func InitFirebase() {
	opt := option.WithCredentialsFile("resell-3afcc-firebase-adminsdk-fbsvc-5d0b6bc88b.json")
	conf := &firebase.Config{ProjectID: "resell-3afcc"}
	app, err := firebase.NewApp(context.Background(), conf, opt)
	if err != nil {
		log.Fatalf("error initializing Firebase app: %v", err)
		return
	}

	fcmClient, err = app.Messaging(context.Background())
	if err != nil {
		log.Fatalf("error initializing Messaging client: %v", err)
		return
	}

	firebaseAuthClient, err = app.Auth(context.Background())
	if err != nil {
		log.Fatalf("error initializing Auth client: %v", err)
		return
	}
}

func InitFCMHandler() {
	notificationRepo := repository.NewNotificationRepository(config.DB)
	FcmHandler = NewFCMHandler(notificationRepo)
}

func VerifyFirebaseIDToken(idToken string) (*auth.Token, error) {
	return firebaseAuthClient.VerifyIDToken(context.Background(), idToken)
}
