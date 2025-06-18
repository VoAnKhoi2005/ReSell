package fb

import (
	"context"
	"firebase.google.com/go/auth"
	"firebase.google.com/go/messaging"
	"log"

	firebase "firebase.google.com/go"
	"google.golang.org/api/option"
)

var fcmClient *messaging.Client
var firebaseAuthClient *auth.Client

func InitFirebase() {
	opt := option.WithCredentialsFile("resell-3afcc-firebase-adminsdk-fbsvc-5d0b6bc88b.json")
	app, err := firebase.NewApp(context.Background(), nil, opt)
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

func VerifyFirebaseIDToken(idToken string) (*auth.Token, error) {
	return firebaseAuthClient.VerifyIDToken(context.Background(), idToken)
}
