package repository

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/util"
	"gorm.io/gorm"
)

type CategoryRepository interface {
	//Imherit from base repo
	GetAll() ([]*model.Category, error)
	Create(category *model.Category) error
	Update(category *model.Category) error
	Delete(category *model.Category) error

	//self func
	GetByID(id string) (*model.Category, error)
	GetByParentID(parentID string) ([]*model.Category, error)
}

type categoryRepository struct {
	*BaseRepository[model.Category] // embed
}

func NewCategoryRepository(db *gorm.DB) CategoryRepository {
	return &categoryRepository{
		BaseRepository: NewBaseRepository[model.Category](db),
	}
}

func (r *categoryRepository) GetByID(id string) (*model.Category, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var category model.Category
	err := r.db.WithContext(ctx).First(&category, "id = ?", id).Error
	return &category, err
}

func (r *categoryRepository) GetByParentID(parentID string) ([]*model.Category, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var categories []*model.Category
	err := r.db.WithContext(ctx).Where("parent_category_id = ?", parentID).Find(&categories).Error
	return categories, err
}
