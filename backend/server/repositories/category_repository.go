package repositories

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/utils"
	"gorm.io/gorm"
)

type CategoryRepository interface {
	GetAll() ([]*models.Category, error)
	GetByID(id uint) (*models.Category, error)
	Create(category *models.Category) error
	Update(id uint, category *models.Category) error
	Delete(id uint) error
}

type categoryRepository struct {
	db *gorm.DB
}

func NewCategoryRepository(db *gorm.DB) CategoryRepository {
	return &categoryRepository{db: db}
}

func (r categoryRepository) GetAll() ([]*models.Category, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var categories []*models.Category
	err := r.db.WithContext(ctx).Find(&categories).Error
	return categories, err
}

func (r categoryRepository) GetByID(id uint) (*models.Category, error) {
	//TODO implement me
	panic("implement me")
}

func (r categoryRepository) Create(category *models.Category) error {
	//TODO implement me
	panic("implement me")
}

func (r categoryRepository) Update(id uint, category *models.Category) error {
	//TODO implement me
	panic("implement me")
}

func (r categoryRepository) Delete(id uint) error {
	//TODO implement me
	panic("implement me")
}
