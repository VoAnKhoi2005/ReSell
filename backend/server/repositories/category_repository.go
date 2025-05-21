package repositories

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/utils"
	"gorm.io/gorm"
)

type CategoryRepository interface {
	//Imherit from base repo
	GetAll() ([]*models.Category, error)
	Create(category *models.Category) error
	Update(category *models.Category) error
	Delete(category *models.Category) error

	//self func
	GetByID(id uint) (*models.Category, error)
}

type categoryRepository struct {
	*BaseRepository[models.Category] // embed
}

func NewCategoryRepository(db *gorm.DB) CategoryRepository {
	return &categoryRepository{
		BaseRepository: NewBaseRepository[models.Category](db),
	}
}

func (r *categoryRepository) GetByID(id uint) (*models.Category, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var category models.Category
	err := r.db.WithContext(ctx).First(&category, "id = ?", id).Error
	return &category, err
}
