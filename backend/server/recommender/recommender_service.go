package recommender

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"gorm.io/gorm"
	"math"
	"sort"
	"time"
)

type Service interface {
	PrepareUserProfile(userID string) (*UserProfile, error)
	PreparePostFeature(postID string, userProfile *UserProfile) (*PostFeature, error)
}

type recommenderService struct {
	db *gorm.DB
}

func NewRecommenderService(db *gorm.DB) Service {
	return &recommenderService{db: db}
}

func (r *recommenderService) PrepareUserProfile(userID string) (*UserProfile, error) {
	var likedPosts []model.Post
	err := r.db.
		Joins("JOIN favorite_posts ON favorite_posts.post_id = posts.id").
		Where("favorite_posts.user_id = ?", userID).
		Preload("Category").
		Find(&likedPosts).Error
	if err != nil {
		return nil, err
	}

	var boughtPosts []model.Post
	err = r.db.
		Joins("JOIN shop_orders ON shop_orders.post_id = posts.id").
		Where("shop_orders.user_id = ? AND shop_orders.status = ?", userID, "completed").
		Preload("Category").
		Find(&boughtPosts).Error
	if err != nil {
		return nil, err
	}

	var addresses []model.Address
	err = r.db.
		Where("user_id = ?", userID).
		Preload("Ward.District.Province").
		Find(&addresses).Error
	if err != nil {
		return nil, err
	}

	// --- Avg price from liked posts ---
	var totalPrice float64
	for _, p := range likedPosts {
		totalPrice += float64(p.Price)
	}
	avgPrice := 0.0
	if len(likedPosts) > 0 {
		avgPrice = totalPrice / float64(len(likedPosts))
	}

	// --- Preferred Categories ---
	categoryCounts := make(map[string]int)
	for _, p := range append(likedPosts, boughtPosts...) {
		if p.CategoryID != nil {
			categoryCounts[*p.CategoryID]++
		}
	}

	// sort by frequency
	type kv struct {
		Key   string
		Value int
	}
	var sorted []kv
	for k, v := range categoryCounts {
		sorted = append(sorted, kv{k, v})
	}
	sort.Slice(sorted, func(i, j int) bool {
		return sorted[i].Value > sorted[j].Value
	})
	var topCategories []string
	for i := 0; i < len(sorted) && i < 5; i++ {
		topCategories = append(topCategories, sorted[i].Key)
	}

	// --- Liked post titles & descriptions ---
	var titles []string
	var descriptions []string
	for _, post := range likedPosts {
		titles = append(titles, post.Title)
		descriptions = append(descriptions, post.Description)
	}

	profile := &UserProfile{
		UserID:               userID,
		AvgPriceLiked:        avgPrice,
		PreferredCategories:  topCategories,
		Addresses:            addresses,
		LikedPostTitle:       titles,
		LikedPostDescription: descriptions,
	}
	return profile, nil
}

func (r *recommenderService) PreparePostFeature(postID string, userProfile *UserProfile) (*PostFeature, error) {
	var post model.Post
	err := r.db.
		Preload("User").
		Preload("Category").
		Preload("Address.Ward.District.Province").
		Where("id = ?", postID).
		First(&post).Error
	if err != nil {
		return nil, err
	}

	// 2. Category match
	categoryScore := CategoryMatchScore(*post.CategoryID, userProfile.PreferredCategories)

	// 3. Price match
	priceScore := 1.0 - math.Min(1.0, math.Abs(float64(post.Price)-userProfile.AvgPriceLiked)/userProfile.AvgPriceLiked)

	// 4. Location distance
	locScore := ComputeLocationDistance(userProfile.Addresses, post.Address)

	// 5. Follower reputation
	sellerReputation := 0.0
	if post.User != nil {
		sellerReputation = float64(post.User.Reputation)
	}

	// 6. Engagement & hotness (placeholders or precomputed)
	engagementScore := EstimateEngagementScore(postID)
	hotnessScore := EstimatePostHotness(post.CreatedAt)

	// 7. Age
	postAgeDays := time.Since(post.CreatedAt).Hours() / 24

	return &PostFeature{
		PostID:                post.ID,
		CategoryMatchScore:    categoryScore,
		PriceMatchScore:       priceScore,
		LocationDistanceScore: locScore,
		SellerReputation:      sellerReputation,
		EngagementScore:       engagementScore,
		PostHotnessScore:      hotnessScore,
		PostAgeDays:           postAgeDays,
		Title:                 post.Title,
		Description:           post.Description,
	}, nil
}

func EstimatePostHotness(at time.Time) float64 {
	return 0
}

func EstimateEngagementScore(id string) float64 {
	return 0
}

func CategoryMatchScore(postCat string, preferred []string) float64 {
	for i, cat := range preferred {
		if cat == postCat {
			return math.Max(0, 1.0-float64(i)*0.1)
		}
	}
	return 0.0
}

func ComputeLocationDistance(userAddresses []model.Address, postAddr *model.Address) float64 {
	// Compare ward/district/province ID, return 0.0 to 1.0
	// You already wrote this or I can generate it
	return 0.5 // placeholder
}
