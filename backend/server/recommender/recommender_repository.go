package recommender

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
	"math"
	"time"
)

const (
	wCategoryMatch       = 1.0
	wPriceMatch          = 0.8
	wLocationDistance    = 0.6
	wEngagement          = 1.2
	wPostHotness         = 1.0
	wPostAge             = -0.5
	wTitleKeywordOverlap = 1.5
	wDescriptionKeyword  = 1.0
	wPostImageScore      = 1.3
)

type Repository interface {
	GetBuyerProfile(userID string) (*dto.BuyerProfile, error)
	GetPostFeatures(postID string, buyerProfile *dto.BuyerProfile) (*dto.PostFeatures, error)
	GetCandidatePostsID(page int, pageSize int) ([]string, error)
}

type recommenderRepository struct {
	db *gorm.DB
}

func NewRecommenderRepository(db *gorm.DB) Repository {
	return &recommenderRepository{db: db}
}

func (r *recommenderRepository) GetBuyerProfile(userID string) (*dto.BuyerProfile, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	db := r.db.WithContext(ctx)
	profile := &dto.BuyerProfile{UserID: userID}

	//AvgPriceLiked
	if err := db.Raw(`
		SELECT COALESCE(AVG(p.price), 0)
		FROM favorite_posts f
		JOIN posts p ON f.post_id = p.id
		WHERE f.user_id = ?
	`, userID).Scan(&profile.AvgPriceLiked).Error; err != nil {
		return nil, err
	}

	//PreferredCategories
	if err := db.Raw(`
		SELECT DISTINCT p.category_id
		FROM favorite_posts f
		JOIN posts p ON f.post_id = p.id
		WHERE f.user_id = ?
		AND p.category_id IS NOT NULL
	`, userID).Scan(&profile.PreferredCategories).Error; err != nil {
		return nil, err
	}

	//Addresses
	var addresses []model.Address
	if err := db.Preload("Ward.District.Province").Find(&addresses, "user_id = ?", userID).Error; err != nil {
		return nil, err
	}
	profile.Addresses = addresses

	//LikedPostTitle
	if err := db.Raw(`
		SELECT p.title
		FROM favorite_posts f
		JOIN posts p ON f.post_id = p.id
		WHERE f.user_id = ?
	`, userID).Scan(&profile.LikedPostTitle).Error; err != nil {
		return nil, err
	}

	//LikedPostDescription
	if err := db.Raw(`
		SELECT p.description
		FROM favorite_posts f
		JOIN posts p ON f.post_id = p.id
		WHERE f.user_id = ?
	`, userID).Scan(&profile.LikedPostDescription).Error; err != nil {
		return nil, err
	}

	return profile, nil
}

func (r *recommenderRepository) GetPostFeatures(postID string, buyerProfile *dto.BuyerProfile) (*dto.PostFeatures, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	db := r.db.WithContext(ctx)
	var post model.Post
	err := r.db.WithContext(ctx).Preload("PostImages").First("id = ?", postID, &post).Error
	if err != nil {
		return nil, err
	}

	//Calculating data
	data := &dto.PostDataForScoring{PostID: postID}
	if err = db.Raw(`
		SELECT title, description, u.reputation
		FROM posts p
		JOIN users u On p.user_id = u.id
		WHERE p.id = ?
		`, postID).Row().Scan(
		&data.Title,
		&data.Description,
		&data.SellerReputation,
	); err != nil {
		return nil, err
	}

	var imageURLs []*string
	for _, postImage := range post.PostImages {
		imageURLs = append(imageURLs, &postImage.ImageURL)
	}
	data.PostImagesURL = imageURLs

	//Calculating feature
	feature := &dto.PostFeatures{PostID: postID}
	var categoryMatch float64
	if err = db.Raw(`
		SELECT 
			CASE WHEN EXISTS (
				SELECT 1 
				FROM shop_orders so
				JOIN posts p2 ON so.post_id = p2.id
				WHERE so.user_id = ? AND p2.category_id = p.category_id
			) THEN 1.0 ELSE 0.0 END AS score
		FROM posts p
		WHERE p.id = ?
		`, buyerProfile.UserID, postID).Row().Scan(&categoryMatch); err != nil {
		return nil, err
	}
	feature.CategoryMatchScore = categoryMatch

	if buyerProfile.AvgPriceLiked > 0 && post.Price > 0 {
		diff := math.Abs(float64(post.Price) - buyerProfile.AvgPriceLiked)
		feature.PriceMatchScore = 1.0 - math.Min(diff/float64(post.Price), 1.0)
	} else {
		feature.PriceMatchScore = 0
	}

	if post.WardID != nil {
		var postWard *model.Ward
		err = db.Preload("District.Province").First(&postWard, "id = ?", post.WardID).Error

		if err == nil {
			for _, userAddr := range buyerProfile.Addresses {
				if userAddr.Ward != nil && postWard != nil {
					if userAddr.Ward.ID == postWard.ID {
						feature.LocationDistanceScore = 1.0
						break
					} else if userAddr.Ward.DistrictID == postWard.DistrictID {
						feature.LocationDistanceScore = 0.7
					} else if userAddr.Ward.District != nil && postWard.District != nil &&
						userAddr.Ward.District.ProvinceID == postWard.District.ProvinceID {
						if feature.LocationDistanceScore < 0.4 {
							feature.LocationDistanceScore = 0.4
						}
					}
				}
			}
		}
	}

	var engagementCount int
	if err = db.
		Raw(`SELECT COUNT(*) FROM favorite_posts WHERE post_id = ?`, postID).
		Scan(&engagementCount).Error; err != nil {
		return nil, err
	}
	feature.EngagementScore = float64(engagementCount)

	feature.PostAgeDays = time.Since(post.CreatedAt).Hours() / 24.0

	if feature.PostAgeDays > 0 {
		feature.PostHotnessScore = feature.EngagementScore / (feature.PostAgeDays + 1)
	} else {
		feature.PostHotnessScore = feature.EngagementScore
	}

	req := &transaction.ScoringRequest{
		PostData:     *data,
		BuyerProfile: *buyerProfile,
	}
	response, err := util.CallPythonScoreAPI(req)
	if err != nil {
		return nil, err
	}

	feature.TitleKeywordOverlap = response.TitleKeywordOverlap
	feature.DescriptionKeywordOverlap = response.DescriptionKeywordOverlap
	feature.PostImageScore = response.ImageTextScore

	feature.FinalScore = feature.CategoryMatchScore*wCategoryMatch +
		feature.PriceMatchScore*wPriceMatch +
		feature.LocationDistanceScore*wLocationDistance +
		feature.EngagementScore*wEngagement +
		feature.PostHotnessScore*wPostHotness +
		feature.PostAgeDays*wPostAge +
		feature.TitleKeywordOverlap*wTitleKeywordOverlap +
		feature.DescriptionKeywordOverlap*wDescriptionKeyword +
		feature.PostImageScore*wPostImageScore

	return feature, nil
}

func (r *recommenderRepository) GetCandidatePostsID(page int, pageSize int) ([]string, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	offset := (page - 1) * pageSize
	var postsID []string

	err := r.db.WithContext(ctx).Raw(`
		SELECT p.id
		FROM posts p
		JOIN users u ON p.user_id = u.id
		WHERE p.status = 'available'
		  AND p.created_at >= NOW() - INTERVAL '14 days'
		  AND u.reputation >= 10
		  AND EXISTS (
		    SELECT 1 FROM post_images pi WHERE pi.post_id = p.id
		  )
		ORDER BY (
		    SELECT COUNT(*) FROM favorite_posts fp WHERE fp.post_id = p.id
		) DESC, p.created_at DESC
		LIMIT ? OFFSET ?;
	`, pageSize, offset).Scan(&postsID).Error

	return postsID, err
}
