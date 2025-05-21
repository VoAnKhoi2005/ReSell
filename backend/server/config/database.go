package config

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/model"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"log"
	"os"
)

var DB *gorm.DB

// ConnectDatabase initializes the database connection using GORM
func ConnectDatabase() {
	dsn := fmt.Sprintf(
		"host=%s user=%s password=%s dbname=%s port=%s sslmode=require",
		os.Getenv("DB_HOST"),
		os.Getenv("DB_USER"),
		os.Getenv("DB_PASSWORD"),
		os.Getenv("DB_NAME"),
		os.Getenv("DB_PORT"))

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Fatal("Failed to connect to the database: ", err)
	}

	DB = db
	log.Println("Connected to PostgresSQL successfully!")
}

func RunMigrations() {
	err := DB.AutoMigrate(
		&model.User{},
		&model.Admin{},
		&model.Post{},
		&model.PostImage{},
		&model.Category{},
		&model.Province{},
		&model.District{},
		&model.Ward{},
		&model.Address{},
		&model.Cart{},
		&model.CartItem{},
		&model.Wallet{},
		&model.PaymentMethod{},
		&model.ShopOrder{},
		&model.UserReview{},
		&model.WalletTransaction{},
		&model.Conversation{},
		&model.Message{},
		&model.Follow{},
	)
	if err != nil {
		panic("Failed to migrate database: " + err.Error())
	}
}
