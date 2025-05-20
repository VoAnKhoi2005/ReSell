package config

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/models"
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
		&models.User{},
		&models.Admin{},
		&models.Post{},
		&models.PostImage{},
		&models.Category{},
		&models.Province{},
		&models.District{},
		&models.Ward{},
		&models.Address{},
		&models.Cart{},
		&models.CartItem{},
		&models.Wallet{},
		&models.PaymentMethod{},
		&models.ShopOrder{},
		&models.UserReview{},
		&models.WalletTransaction{},
		&models.Conversation{},
		&models.Message{},
		&models.Follow{},
	)
	if err != nil {
		panic("Failed to migrate database: " + err.Error())
	}
}
