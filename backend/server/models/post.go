package models

import (
	"gorm.io/gorm"
	"time"
)

type Category struct {
	ID               uint
	ParentCategoryID uint
	Name             string
	ParentCategory   *Category
}

type Post struct {
	gorm.Model
	UserID      uint
	CategoryID  uint
	AddressID   uint
	Title       string
	Description string
	Price       uint
	Status      string
	SoldAt      time.Time
	User        *User
	Category    *Category
	Address     *Address
}

type PostImage struct {
	PostID     uint   `gorm:"primaryKey"`
	ImageURL   string `gorm:"primaryKey"`
	ImageOrder uint
}
