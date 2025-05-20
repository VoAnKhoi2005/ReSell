package service

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/repositories"
)

type UserService interface {
	Register(user *models.User) error
	GetUserByID(id string) (*models.User, error)
	GetUserByUsername(username string) (*models.User, error)
	GetUserByEmail(email string) (*models.User, error)
}

type userService struct {
	repo repositories.UserRepository
}

func NewUserService(repo repositories.UserRepository) UserService {
	return &userService{repo: repo}
}

func (s userService) Register(user *models.User) error {
	return s.repo.Create(user)
}

func (s userService) GetUserByID(id string) (*models.User, error) {
	return s.repo.GetByID(id)
}

func (s userService) GetUserByUsername(username string) (*models.User, error) {
	return s.repo.GetByUsername(username)
}

func (s userService) GetUserByEmail(email string) (*models.User, error) {
	return s.repo.GetByEmail(email)
}
