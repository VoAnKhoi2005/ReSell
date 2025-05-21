package model

import (
	"gorm.io/gorm"
	"time"
)

type Category struct {
	ID               string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	ParentCategoryID string `gorm:"type:uuid"`
	Name             string

	ParentCategory *Category
}

type Post struct {
	ID          string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	UserID      string `gorm:"type:uuid"`
	CategoryID  string `gorm:"type:uuid"`
	AddressID   string `gorm:"type:uuid"`
	Title       string
	Description string
	Price       uint
	Status      string
	SoldAt      time.Time
	CreatedAt   time.Time
	UpdatedAt   time.Time
	DeletedAt   gorm.DeletedAt `gorm:"index"`

	User     *User
	Category *Category
	Address  *Address
}

type PostImage struct {
	PostID     string `gorm:"type:uuid;primaryKey"`
	ImageURL   string `gorm:"primaryKey"`
	ImageOrder uint
}
