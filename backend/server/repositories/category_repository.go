package repositories

import "github.com/VoAnKhoi2005/ReSell/models"

type CategoryRepository interface {
	GetAll() ([]*models.Category, error)
	GetByID(id uint) (*models.Category, error)
	Create(category *models.Category) error
	Update(id uint, category *models.Category) error
	Delete(id uint) error
}
