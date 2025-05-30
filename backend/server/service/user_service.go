package service

import (
	"errors"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"golang.org/x/crypto/bcrypt"
	"strings"
	"time"
)

type UserService interface {
	Register(user *model.User) error
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

func (s *userService) Register(user *model.User) error {
	var validationErrors []string

	if _, err := s.userRepo.GetByEmail(user.Email); err == nil {
		validationErrors = append(validationErrors, "email: email already taken")
	}

	if _, err := s.userRepo.GetByPhone(user.Phone); err == nil {
		validationErrors = append(validationErrors, "phone: phone number already taken")
	}

	if _, err := s.userRepo.GetByUsername(user.Username); err == nil {
		validationErrors = append(validationErrors, "username: username already taken")
	}

	if len(validationErrors) > 0 {
		return fmt.Errorf(strings.Join(validationErrors, ", "))
	}

	err := s.userRepo.Create(user)
	if err != nil {
		return err
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
	return s.userRepo.FollowUser(followerID, followeeID)
}

func (s *userService) BanUserForDay(userID string, length uint) error {
	if length < 1 {
		return errors.New("invalid length")
	}

	user, err := s.userRepo.GetByID(userID)
	if err != nil {
		return err
	}

	banStart := time.Now()
	banEnd := banStart.Add(time.Duration(length) * time.Hour * 24)

	user.BanStart = &banStart
	user.BanEnd = &banEnd

	return s.userRepo.Update(user)
}

func (s *userService) UnBanUser(userID string) error {
	user, err := s.userRepo.GetByID(userID)
	if err != nil {
		return err
	}

	user.BanStart = nil
	user.BanEnd = nil
	return s.userRepo.Update(user)
}
