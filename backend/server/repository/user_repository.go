package repository

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
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

	FollowUser(followerID *string, followedID *string) error
	GetAllFollowUser(followerID *string) ([]*model.User, error)
	UnFollowUser(followerID *string, followedID *string) error

	GetStripeAccountID(userID string) (string, error)
	GetByStripeAccountID(accountID string) (*model.User, error)

	GetStat(userID string) (*dto.UserStatDTO, error)
}

type userRepository struct {
	*BaseRepository[model.User]
}

func (r *userRepository) GetStat(userID string) (*dto.UserStatDTO, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var stat dto.UserStatDTO

	if err := r.db.WithContext(ctx).Raw(`SELECT COUNT(*) FROM posts WHERE user_id = ?`, userID).Scan(&stat.PostNumber).Error; err != nil {
		return nil, err
	}

	if err := r.db.WithContext(ctx).Raw(`SELECT COUNT(*) FROM shop_orders WHERE user_id = ?`, userID).Scan(&stat.BoughtNumber).Error; err != nil {
		return nil, err
	}

	row := r.db.WithContext(ctx).Raw(`
		SELECT COUNT(*), COALESCE(SUM(total), 0) 
		FROM shop_orders s 
		JOIN posts p ON s.post_id = p.id 
		WHERE p.user_id = ? AND s.status = 'completed'
	`, userID).Row()
	if err := row.Scan(&stat.SelledNumber, &stat.Revenue); err != nil {
		return nil, err
	}

	if err := r.db.WithContext(ctx).Raw(`SELECT COUNT(*) FROM report_users WHERE reported_id = ?`, userID).Scan(&stat.ReportNumber).Error; err != nil {
		return nil, err
	}

	return &stat, nil
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

func (r *userRepository) FollowUser(followerID *string, followedID *string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Create(&model.Follow{BuyerID: followerID, SellerID: followedID}).Error
}

func (r *userRepository) GetAllFollowUser(followerID *string) ([]*model.User, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var users []*model.User = nil
	err := r.db.WithContext(ctx).Find(&users, "buyer_id = ?", followerID).Error
	return users, err
}

func (r *userRepository) UnFollowUser(followerID *string, followeeID *string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var follow *model.Follow = nil
	err := r.db.WithContext(ctx).First(&follow, "buyer_id = ? AND seller_id = ?", followerID, followeeID).Error
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
