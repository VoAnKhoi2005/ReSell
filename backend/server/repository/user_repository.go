package repository

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
	"time"
)

type UserRepository interface {
	//generic func
	GetAll() ([]*model.User, error)
	Create(user *model.User) error
	Update(user *model.User) error
	Delete(user *model.User) error

	//self func
	GetByID(id string) (*model.User, error)
	GetByUsername(username string) (*model.User, error)
	GetByEmail(email string) (*model.User, error)
	GetByPhone(phone string) (*model.User, error)
	GetByFirebaseUID(firebaseUID string) (*model.User, error)
	GetUsersByBatch(batchSize int, page int) ([]*model.User, int, error)

	DeleteByID(id string) error

	FollowUser(follow *model.Follow) error
	GetAllFollowUser(followerID *string) ([]*model.User, error)
	UnFollowUser(followerID *string, followedID *string) error

	GetStripeAccountID(userID string) (string, error)
	GetByStripeAccountID(accountID string) (*model.User, error)

	GetStat(userID string, requesterID string) (*dto.UserStatDTO, error)
	SearchUsername(query string) ([]*model.User, error)

	IncreaseReputation(userID string, amount int) error
	DecreaseReputation(userID string, amount int) error
}

type userRepository struct {
	*BaseRepository[model.User]
}

func NewUserRepository(db *gorm.DB) UserRepository {
	return &userRepository{
		BaseRepository: NewBaseRepository[model.User](db),
	}
}

func (r *userRepository) GetByID(id string) (*model.User, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var user *model.User = nil
	err := r.db.WithContext(ctx).First(&user, "id = ?", id).Error
	return user, err
}

func (r *userRepository) GetByUsername(username string) (*model.User, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var user *model.User = nil
	err := r.db.WithContext(ctx).First(&user, "username = ?", username).Error
	return user, err
}

func (r *userRepository) GetByEmail(email string) (*model.User, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var user *model.User = nil
	err := r.db.WithContext(ctx).First(&user, "email = ?", email).Error
	return user, err
}

func (r *userRepository) GetByPhone(phone string) (*model.User, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var user *model.User = nil
	err := r.db.WithContext(ctx).First(&user, "phone = ?", phone).Error
	return user, err
}

func (r *userRepository) GetByFirebaseUID(firebaseUID string) (*model.User, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var user *model.User = nil
	err := r.db.WithContext(ctx).First(&user, "firebase_uid = ?", firebaseUID).Error
	return user, err
}

func (r *userRepository) GetUsersByBatch(batchSize int, page int) ([]*model.User, int, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var totalCount int64
	err := r.db.WithContext(ctx).Model(&model.User{}).Count(&totalCount).Error
	if err != nil {
		return nil, 0, err
	}

	totalBatches := int((totalCount + int64(batchSize) - 1) / int64(batchSize))
	offset := (page - 1) * batchSize

	if totalBatches == 0 {
		totalBatches = 1
	}

	if page > totalBatches {
		return nil, totalBatches, fmt.Errorf("page %d out of range: total pages %d", page, totalBatches)
	}

	var users []*model.User = nil
	err = r.db.WithContext(ctx).Order("id").Limit(batchSize).Offset(offset).Find(&users).Error
	if err != nil {
		return nil, 0, err
	}

	return users, totalBatches, err
}

func (r *userRepository) DeleteByID(id string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Delete(&model.User{}, "id = ?", id).Error
}

func (r *userRepository) FollowUser(follow *model.Follow) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Create(&follow).Error
}

func (r *userRepository) GetAllFollowUser(followerID *string) ([]*model.User, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var users []*model.User = nil
	err := r.db.WithContext(ctx).Find(&users, "followee_id = ?", followerID).Error
	return users, err
}

func (r *userRepository) UnFollowUser(followerID *string, followeeID *string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var follow *model.Follow = nil
	err := r.db.WithContext(ctx).First(&follow, "follower_id = ? AND followee_id = ?", followerID, followeeID).Error
	if err != nil {
		return err
	}

	return r.db.WithContext(ctx).Unscoped().Delete(&follow).Error
}

func (r *userRepository) GetStripeAccountID(userID string) (string, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var user model.User
	err := r.db.WithContext(ctx).Select("stripe_account_id").First(&user, "id = ?", userID).Error
	return user.StripeAccountIDValue(), err
}

func (r *userRepository) GetByStripeAccountID(accountID string) (*model.User, error) {
	var user model.User
	if err := r.db.Where("stripe_account_id = ?", accountID).First(&user).Error; err != nil {
		return nil, err
	}
	return &user, nil
}

func (r *userRepository) GetStat(userID string, requesterID string) (*dto.UserStatDTO, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	db := r.db.WithContext(ctx)
	stat := &dto.UserStatDTO{UserID: userID}

	if err := db.Raw(`
		SELECT u.username, u.fullname, u.avatar_url, u.cover_url, u.created_at
		FROM users u
		WHERE id = ?
	`, userID).Row().Scan(&stat.Username, &stat.FullName, &stat.AvatarURL, &stat.CoverURL, &stat.CreatedAt); err != nil {
		return nil, err
	}

	if err := db.Raw(`
	SELECT
		COUNT(sr.conversation_id) * 100.0 / NULLIF(COUNT(bm.conversation_id), 0) AS seller_response_percentage
	FROM (
		SELECT c.id AS conversation_id, c.seller_id, c.buyer_id
		FROM conversations c
		WHERE c.seller_id = ?
	) AS sc
	LEFT JOIN (
		SELECT m.conversation_id, MIN(m.created_at) AS buyer_first_msg_time
		FROM messages m
		JOIN conversations c ON c.id = m.conversation_id
		WHERE m.sender_id = c.buyer_id
		GROUP BY m.conversation_id
	) AS bm ON bm.conversation_id = sc.conversation_id
	LEFT JOIN (
		SELECT DISTINCT m.conversation_id
		FROM messages m
		JOIN conversations c ON c.id = m.conversation_id
		WHERE m.sender_id = c.seller_id
		  AND m.created_at > (
			SELECT MIN(m2.created_at)
			FROM messages m2
			WHERE m2.conversation_id = m.conversation_id AND m2.sender_id = c.buyer_id
		  )
	) AS sr ON sr.conversation_id = bm.conversation_id;
	`, userID).Scan(&stat.ChatResponsePercentage).Error; err != nil {
		return nil, err
	}

	// Posts and average post price
	if err := db.Raw(`
		SELECT COUNT(*), COALESCE(AVG(price), 0)
		FROM posts
		WHERE user_id = ?
	`, userID).Row().Scan(&stat.PostNumber, &stat.AveragePostPrice); err != nil {
		return nil, err
	}

	// Orders placed by user
	if err := db.Raw(`
		SELECT COUNT(*)
		FROM shop_orders
		WHERE user_id = ?
	`, userID).Scan(&stat.BoughtNumber).Error; err != nil {
		return nil, err
	}

	// Sales and revenue (as seller)
	if err := db.Raw(`
		SELECT COUNT(*), COALESCE(SUM(s.total), 0)
		FROM shop_orders s
		JOIN posts p ON s.post_id = p.id
		WHERE p.user_id = ? AND s.status = 'completed'
	`, userID).Row().Scan(&stat.SaleNumber, &stat.TotalRevenue); err != nil {
		return nil, err
	}

	// Reports against user
	if err := db.Raw(`
		SELECT COUNT(*)
		FROM report_users
		WHERE reported_id = ?
	`, userID).Scan(&stat.ReportNumber).Error; err != nil {
		return nil, err
	}

	// Reviews: average rating and total count
	if err := db.Raw(`
		SELECT COALESCE(AVG(rating), 0), COUNT(*)
		FROM user_reviews
		WHERE user_id = ?
	`, userID).Row().Scan(&stat.AverageRating, &stat.ReviewNumber); err != nil {
		return nil, err
	}

	// Followers and followees
	if err := db.Raw(`
		SELECT 
			(SELECT COUNT(*) FROM follows WHERE followee_id = ?) AS followers,
			(SELECT COUNT(*) FROM follows WHERE follower_id = ?) AS followees
	`, userID, userID).Row().Scan(&stat.FollowerCount, &stat.FolloweeCount); err != nil {
		return nil, err
	}

	// Reputation
	if err := db.Raw(`
		SELECT reputation
		FROM users
		WHERE id = ?
	`, userID).Scan(&stat.Reputation).Error; err != nil {
		return nil, err
	}

	// Last activity time (latest post)
	var lastActivity time.Time
	if err := db.Raw(`
		SELECT COALESCE(MAX(created_at), '0001-01-01 00:00:00')
		FROM posts
		WHERE user_id = ?
	`, userID).Scan(&lastActivity).Error; err != nil {
		return nil, err
	}
	if lastActivity.IsZero() || lastActivity.Equal(time.Date(1, 1, 1, 0, 0, 0, 0, time.UTC)) {
		stat.LastActivity = nil
	} else {
		stat.LastActivity = &lastActivity
	}

	var follow model.Follow
	err := db.WithContext(ctx).
		Model(&model.Follow{}).
		Where("follower_id = ? AND followee_id = ?", requesterID, userID).
		First(&follow).Error
	stat.IsFollowing = err == nil

	return stat, nil
}

func (r *userRepository) SearchUsername(query string) ([]*model.User, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var users []*model.User
	err := r.db.WithContext(ctx).Where("username ILIKE ?", query).Find(&users).Error
	return users, err
}

func (r *userRepository) IncreaseReputation(userID string, amount int) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	user, err := r.GetByID(userID)
	if err != nil {
		return err
	}

	user.Reputation = user.Reputation + amount
	return r.db.WithContext(ctx).Save(user).Error
}

func (r *userRepository) DecreaseReputation(userID string, amount int) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	user, err := r.GetByID(userID)
	if err != nil {
		return err
	}

	user.Reputation = user.Reputation - amount
	return r.db.WithContext(ctx).Save(user).Error
}
