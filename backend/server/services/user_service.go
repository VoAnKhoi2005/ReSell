package services

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/repositories"
)

type UserService interface {
	Register(user *models.User) error
	GetUserByID(userID uint) (*models.User, error)
	GetUserByUsername(username string) (*models.User, error)
	GetUserByEmail(email string) (*models.User, error)
	GetUserByPhone(phone string) (*models.User, error)
}

type userService struct {
	UserRepository repositories.UserRepository
}

func NewUserService(repo repositories.UserRepository) UserService {
	return &userService{UserRepository: repo}
}

func (s userService) Register(user *models.User) error {
	return s.UserRepository.Create(user)
}

func (s userService) GetUserByID(userID uint) (*models.User, error) {
	return s.UserRepository.GetByID(userID)
}

func (s userService) GetUserByUsername(username string) (*models.User, error) {
	return s.UserRepository.GetByUsername(username)
}

func (s userService) GetUserByEmail(email string) (*models.User, error) {
	return s.UserRepository.GetByEmail(email)
}

func (s userService) GetUserByPhone(phone string) (*models.User, error) {
	return s.UserRepository.GetByPhone(phone)
}
