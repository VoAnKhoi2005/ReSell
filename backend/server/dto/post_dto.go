package dto

import (
	"time"
)

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
	Address   string    `json:"address"`
	Price     int       `json:"price"`
	CreatedAt time.Time `json:"created_at"`
}

type PostFeatures struct {
	PostID           string  `json:"post_id"`
	Title            string  `json:"title"`
	Description      string  `json:"description"`
	SellerReputation float64 `json:"seller_reputation"`

	CategoryMatchScore    float64 `json:"category_match_score"`
	PriceMatchScore       float64 `json:"price_match_score"`
	LocationDistanceScore float64 `json:"location_distance_score"`
	EngagementScore       float64 `json:"engagement_score"`
	PostHotnessScore      float64 `json:"post_hotness_score"`
	PostAgeDays           float64 `json:"post_age_days"`

	//Calculate in python
	TitleKeywordOverlap       *float64  `json:"title_keyword_overlap"`
	DescriptionKeywordOverlap *float64  `json:"description_keyword_overlap"`
	PostImagesURL             []*string `json:"post_images_url"`
}
