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
	GetUserByPhone(phone string) (*model.User, error)

	UpdateUser(user *model.User) error

	DeleteUser(user *model.User) error
	DeleteUserByID(ID string) error

	FollowUser(followerID *string, followedID *string) error
}

type userService struct {
	userRepo repository.UserRepository
}

func NewUserService(repo repository.UserRepository) UserService {
	return &userService{userRepo: repo}
}

func (s userService) Register(user *model.User) error {
	return s.userRepo.Create(user)
}

func (s userService) GetUserByID(id string) (*model.User, error) {
	return s.userRepo.GetByID(id)
}

func (s userService) GetUserByUsername(username string) (*model.User, error) {
	return s.userRepo.GetByUsername(username)
}

func (s userService) GetUserByEmail(email string) (*model.User, error) {
	return s.userRepo.GetByEmail(email)
}

func (s userService) GetUserByPhone(phone string) (*model.User, error) {
	return s.userRepo.GetByPhone(phone)
}

func (s userService) UpdateUser(user *model.User) error {
	return s.userRepo.Update(user)
}

func (s userService) DeleteUser(user *model.User) error {
	return s.userRepo.Delete(user)
}

func (s userService) DeleteUserByID(ID string) error {
	return s.userRepo.DeleteByID(ID)
}

func (s userService) FollowUser(followerID *string, followedID *string) error {
	return s.userRepo.FollowUser(followerID, followedID)
}
