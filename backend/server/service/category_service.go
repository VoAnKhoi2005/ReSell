package service

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/google/uuid"
)

type CategoryService interface {
	GetAllCategories() ([]*model.Category, error)
	GetCategoryByID(id string) (*model.Category, error)
	CreateCategory(req *transaction.CreateCategoryRequest) (*model.Category, error)
	UpdateCategory(id string, req *transaction.UpdateCategoryRequest) (*model.Category, error)
	DeleteCategory(id string) error
	GetCategoriesByParentID(parentID string) ([]*model.Category, error)
}

type categoryService struct {
	repo repository.CategoryRepository
}

func (s *categoryService) CreateCategory(req *transaction.CreateCategoryRequest) (*model.Category, error) {
	category := &model.Category{
		ID:               uuid.New().String(),
		Name:             req.Name,
		ParentCategoryID: req.ParentCategoryId,
	}
	err := s.repo.Create(category)

	return category, err
}

func (s *categoryService) UpdateCategory(id string, req *transaction.UpdateCategoryRequest) (*model.Category, error) {
	category, err := s.GetCategoryByID(id)

	if err != nil {
		return nil, err
	}

	category.Name = req.Name
	err = s.repo.Update(category)
	return category, err
}

func (s *categoryService) GetAllCategories() ([]*model.Category, error) {
	return s.repo.GetAll()
}

func (s *categoryService) GetCategoryByID(id string) (*model.Category, error) {
	return s.repo.GetByID(id)
}

func (s *categoryService) DeleteCategory(id string) error {
	category, err := s.repo.GetByID(id)
	if err != nil {
		return err
	}
	return s.repo.Delete(category)
}

func NewCategoryService(repo repository.CategoryRepository) CategoryService {
	return &categoryService{repo: repo}
}

func (s *categoryService) GetCategoriesByParentID(parentID string) ([]*model.Category, error) {
	return s.repo.GetByParentID(parentID)
}
