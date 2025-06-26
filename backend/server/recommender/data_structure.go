package recommender

import "github.com/VoAnKhoi2005/ReSell/backend/server/model"

type PostFeature struct {
	PostID                string  `json:"post_id"`
	CategoryMatchScore    float64 `json:"category_match_score"`
	PriceMatchScore       float64 `json:"price_match_score"`
	LocationDistanceScore float64 `json:"location_distance_score"`
	SellerReputation      float64 `json:"seller_reputation"`
	EngagementScore       float64 `json:"engagement_score"`
	PostHotnessScore      float64 `json:"post_hotness_score"`
	PostAgeDays           float64 `json:"post_age_days"`
	Title                 string  `json:"title"`
	Description           string  `json:"description"`

	//Calculate in python
	TitleKeywordOverlap       *float64  `json:"title_keyword_overlap"`
	DescriptionKeywordOverlap *float64  `json:"description_keyword_overlap"`
	PostImagesURL             []*string `json:"post_images_url"`
}

type BuyerProfile struct {
	UserID               string          `json:"user_id"`
	AvgPriceLiked        float64         `json:"avg_price_liked"`
	PreferredCategories  []string        `json:"preferred_categories"`
	Addresses            []model.Address `json:"addresses"`
	LikedPostTitle       []string        `json:"liked_post_title"`
	LikedPostDescription []string        `json:"liked_post_description"`
}
