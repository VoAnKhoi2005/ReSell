package dto

import "time"

type PostListAdminDTO struct {
	ID       string `json:"id"`
	Title    string `json:"title"`
	Status   string `json:"status"`
	Category string `json:"category"`
	Owner    string `json:"owner"`
}

type PostListUserDTO struct {
	ID        string    `json:"id"`
	Title     string    `json:"title"`
	Status    string    `json:"status"`
	Category  string    `json:"category"`
	Owner     string    `json:"owner"`
	Thumbnail string    `json:"thumbnail"`
	Province  string    `json:"province"`
	Price     int       `json:"price"`
	CreateAt  time.Time `json:"create_at"`
}
