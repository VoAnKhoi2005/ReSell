package repositories

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/utils"
	"gorm.io/gorm"
)

type UserRepository interface {
	//generic func
	GetAll() ([]*models.User, error)
	Create(user *models.User) error
	Update(user *models.User) error
	Delete(user *models.User) error

	//self func
	GetByID(id string) (*models.User, error)
	GetByUsername(username string) (*models.User, error)
	GetByEmail(email string) (*models.User, error)
	GetByPhone(phone string) (*models.User, error)
}

type userRepository struct {
	*BaseRepository[models.User]
}

func NewUserRepository(db *gorm.DB) UserRepository {
	return &userRepository{
		BaseRepository: NewBaseRepository[models.User](db),
	}
}

func (r *userRepository) GetByID(id uint) (*models.User, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var user models.User
	err := r.db.WithContext(ctx).First(&user, "id = ?", id).Error
	return &user, err
}

func (r *userRepository) GetByUsername(username string) (*models.User, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var user models.User
	err := r.db.WithContext(ctx).First(&user, "username = ?", username).Error
	return &user, err
}

func (r *userRepository) GetByEmail(email string) (*models.User, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var user models.User
	err := r.db.WithContext(ctx).First(&user, "email = ?", email).Error
	return &user, err
}

func (r *userRepository) GetByPhone(phone string) (*models.User, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var user models.User
	err := r.db.WithContext(ctx).First(&user, "phone = ?", phone).Error
	return &user, err
}
