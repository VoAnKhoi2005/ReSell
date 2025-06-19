package config

import (
	"github.com/stripe/stripe-go/v78"
	"os"
)

func InitStripe() {
	stripe.Key = os.Getenv("STRIPE_SECRET_KEY")
}
