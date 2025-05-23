package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"golang.org/x/crypto/bcrypt"
)

type UserService interface {
	Register(user *model.User) []string
	Login(identifier string, password string, loginType string) (*model.User, error)

	GetUserByID(id string) (*model.User, error)
	UpdateUser(user *model.User) error
	DeleteUser(user *model.User) error
	DeleteUserByID(ID string) error

	FollowUser(followerID *string, followeeID *string) error
	BanUserForDay(userID string, length uint) error
	UnBanUser(userID string) error
}

type userService struct {
	userRepo repository.UserRepository
}

func NewUserService(repo repository.UserRepository) UserService {
	return &userService{userRepo: repo}
}

func (s *userService) Register(user *model.User) []string {
	var errors []string
	var err error

	_, err = s.userRepo.GetByEmail(user.Email)
	if err == nil {
		errors = append(errors, "email: email already taken")
	}

	_, err = s.userRepo.GetByUsername(user.Username)
	if err == nil {
		errors = append(errors, "phone: phone number already taken")
	}

	_, err = s.userRepo.GetByUsername(user.Username)
	if err == nil {
		errors = append(errors, "username: username already taken")
	}

	if len(errors) > 0 {
		return errors
	}

	err = s.userRepo.Create(user)
	if err != nil {
		errors = append(errors, err.Error())
		return errors
	}

	return nil
}

func (s *userService) Login(identifier string, password string, loginType string) (*model.User, error) {
	var user *model.User
	var err error
	switch loginType {
	case "email":
		user, err = s.userRepo.GetByEmail(identifier)
	case "phone":
		user, err = s.userRepo.GetByPhone(identifier)
	case "username":
		user, err = s.userRepo.GetByUsername(identifier)
	default:
		return nil, errors.New("invalid credentials")
	}

	if err != nil {
		return nil, err
	}

	if bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(password)) != nil {
		return nil, errors.New("invalid credentials")
	}

	return user, nil
}

func (s *userService) GetUserByID(id string) (*model.User, error) {
	return s.userRepo.GetByID(id)
}

func (s *userService) UpdateUser(user *model.User) error {
	return s.userRepo.Update(user)
}

func (s *userService) DeleteUser(user *model.User) error {
	return s.userRepo.Delete(user)
}

func (s *userService) DeleteUserByID(ID string) error {
	return s.userRepo.DeleteByID(ID)
}

func (s *userService) FollowUser(followerID *string, followeeID *string) error {
	var err error

	_, err = s.userRepo.GetByID(*followerID)
	if err != nil {
		return err
	}

	_, err = s.userRepo.GetByID(*followeeID)
	if err != nil {
		return err
	}

	return s.userRepo.FollowUser(followerID, followeeID)
}

func (s *userService) BanUserForDay(userID string, length uint) error {
	if length < 1 {
		return errors.New("invalid length")
	}

	_, err := s.userRepo.GetByID(userID)
	if err != nil {
		return err
	}

	return s.userRepo.BanUserForDay(userID, length)
}

func (s *userService) UnBanUser(userID string) error {
	return s.userRepo.UnBanUser(userID)
}
