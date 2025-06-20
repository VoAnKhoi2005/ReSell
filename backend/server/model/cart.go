package model

type FavoritePost struct {
	UserID *string `gorm:"type:uuid;primaryKey" json:"cart_id"`
	PostID *string `gorm:"type:uuid;primaryKey" json:"post_id"`

	User *User `json:"user,omitempty"`
	Post *Post `json:"post,omitempty"`
}
