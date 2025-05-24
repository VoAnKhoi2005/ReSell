package model

type Cart struct {
	ID     string  `gorm:"type:uuid;primary_key" json:"id"`
	UserID *string `gorm:"type:uuid" json:"user_id"`

	User *User `json:"user,omitempty"`
}

type CartItem struct {
	CartID *string `gorm:"type:uuid;primaryKey" json:"cart_id"`
	PostID *string `gorm:"type:uuid;primaryKey" json:"post_id"`

	Cart *Cart `json:"cart,omitempty"`
	Post *Post `json:"post,omitempty"`
}
