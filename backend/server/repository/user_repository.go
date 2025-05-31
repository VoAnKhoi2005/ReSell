package repository

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/util"
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

	DeleteByID(id string) error

	FollowUser(followerID *string, followedID *string) error
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

func (r *userRepository) DeleteByID(id string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Delete(&model.User{}, "id = ?", id).Error
}

func (r *userRepository) FollowUser(followerID *string, followedID *string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Create(&model.Follow{BuyerId: followerID, SellerId: followedID}).Error
}
