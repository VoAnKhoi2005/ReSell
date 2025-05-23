package model

type Cart struct {
	ID     string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	UserID *string `gorm:"type:uuid"`

	User *User
}

type CartItem struct {
	CartID *string `gorm:"type:uuid;primaryKey"`
	PostID *string `gorm:"type:uuid;primaryKey"`

	Cart *Cart
	Post *Post
}
