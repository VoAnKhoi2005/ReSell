package model

import (
	"gorm.io/gorm"
	"time"
)

type PostStatus string

const (
	PostStatusPending  PostStatus = "pending"
	PostStatusApproved PostStatus = "approved"
	PostStatusRejected PostStatus = "rejected"
	PostStatusSold     PostStatus = "sold"
	PostStatusHidden   PostStatus = "hidden"
	PostStatusDeleted  PostStatus = "deleted"
)

type Category struct {
	ID               string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	ParentCategoryID *string `gorm:"type:uuid;index;constraint:OnDelete:CASCADE;" json:"parent_category_id"`
	Name             string  `json:"name"`

	ParentCategory *Category `json:"parent_category,omitempty"`
}

type Post struct {
	ID          string         `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserID      *string        `gorm:"type:uuid" json:"user_id"`
	CategoryID  *string        `gorm:"type:uuid" json:"category_id"`
	AddressID   *string        `gorm:"type:uuid" json:"address_id"`
	Title       string         `json:"title"`
	Description string         `json:"description"`
	Price       uint           `json:"price"`
	Status      PostStatus     `json:"status"`
	SoldAt      *time.Time     `json:"sold_at,omitempty"`
	CreatedAt   time.Time      `json:"created_at"`
	UpdatedAt   time.Time      `json:"updated_at"`
	DeletedAt   gorm.DeletedAt `gorm:"index" json:"deleted_at,omitempty"`

	User     *User     `json:"user,omitempty"`
	Category *Category `json:"category,omitempty"`
	Address  *Address  `json:"address,omitempty"`
}

type PostImage struct {
	PostID     *string `gorm:"type:uuid;primaryKey" json:"post_id"`
	ImageURL   string  `gorm:"primaryKey" json:"image_url"`
	ImageOrder uint    `json:"image_order"`
}
