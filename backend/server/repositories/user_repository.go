package repositories

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/utils"
	"gorm.io/gorm"
)

type UserRepository interface {
	GetByID(id string) (*models.User, error)
	GetByUsername(username string) (*models.User, error)
	GetByEmail(email string) (*models.User, error)
	Create(user *models.User) error
}

type userRepository struct {
	db *gorm.DB
}

func NewUserRepository(db *gorm.DB) UserRepository {
	return &userRepository{db: db}
}

func (r userRepository) GetByID(id string) (*models.User, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var user models.User
	err := r.db.WithContext(ctx).First(&user, "id = ?", id).Error
	return &user, err
}

func (r userRepository) GetByUsername(username string) (*models.User, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var user models.User
	err := r.db.WithContext(ctx).First(&user, "username = ?", username).Error
	return &user, err
}

func (r userRepository) GetByEmail(email string) (*models.User, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var user models.User
	err := r.db.WithContext(ctx).First(&user, "email = ?", email).Error
	return &user, err
}

func (r userRepository) Create(user *models.User) error {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Create(user).Error
}
