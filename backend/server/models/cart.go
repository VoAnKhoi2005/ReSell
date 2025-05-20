package models

type Cart struct {
	ID     uint
	UserID uint

	User *User
}

type CartItem struct {
	CartID uint `gorm:"primaryKey"`
	PostID uint `gorm:"primaryKey"`

	Cart *Cart
	Post *Post
}
