package services

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/repositories"
	request "github.com/VoAnKhoi2005/ReSell/requests"
)

type CategoryService interface {
	GetAllCategories() ([]*models.Category, error)
	GetCategoryByID(id uint) (*models.Category, error)
	CreateCategory(req *request.CreateCategoryRequest) (*models.Category, error)
	UpdateCategory(id uint, req *request.UpdateCategoryRequest) (*models.Category, error)
	DeleteCategory(id uint) error
}

type categoryService struct {
	repo repositories.CategoryRepository
}

func (s categoryService) CreateCategory(req *request.CreateCategoryRequest) (*models.Category, error) {
	category := &models.Category{Name: req.Name}
	err := s.repo.Create(category)

	return category, err
}

func (s categoryService) UpdateCategory(id uint, req *request.UpdateCategoryRequest) (*models.Category, error) {
	category, err := s.GetCategoryByID(id)

	if err != nil {
		return nil, err
	}

	category.Name = req.Name
	err = s.repo.Update(category)
	return category, err
}

func (s categoryService) GetAllCategories() ([]*models.Category, error) {
	return s.repo.GetAll()
}

func (s categoryService) GetCategoryByID(id uint) (*models.Category, error) {
	return s.repo.GetByID(id)
}

func (s categoryService) DeleteCategory(id uint) error {
	category, err := s.repo.GetByID(id)
	if err != nil {
		return err
	}
	return s.repo.Delete(category)
}

func NewCategoryService(repo repositories.CategoryRepository) CategoryService {
	return &categoryService{repo: repo}
}
