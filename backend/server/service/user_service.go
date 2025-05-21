package service

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
)

type UserService interface {
	Register(user *model.User) error
	GetUserByID(id string) (*model.User, error)
	GetUserByUsername(username string) (*model.User, error)
	GetUserByEmail(email string) (*model.User, error)
}

type userService struct {
	repo repository.UserRepository
}

func NewUserService(repo repository.UserRepository) UserService {
	return &userService{repo: repo}
}

func (s userService) Register(user *model.User) error {
	return s.repo.Create(user)
}

func (s userService) GetUserByID(id string) (*model.User, error) {
	return s.repo.GetByID(id)
}

func (s userService) GetUserByUsername(username string) (*model.User, error) {
	return s.repo.GetByUsername(username)
}

func (s userService) GetUserByEmail(email string) (*model.User, error) {
	return s.repo.GetByEmail(email)
}
