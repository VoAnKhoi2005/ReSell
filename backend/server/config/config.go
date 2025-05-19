package config

import (
	"github.com/joho/godotenv"
	"log"
)

// LoadEnv loads environment variables from .env file
func LoadEnv() {
	err := godotenv.Load()
	if err != nil {
		log.Println(".env file not found")
	}
}
