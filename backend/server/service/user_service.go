package service

import (
	"errors"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
	request "github.com/VoAnKhoi2005/ReSell/transaction"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
	"strings"
	"time"
)

type UserService interface {
	Register(user *model.User) error
	Login(identifier string, password string, loginType string) (*model.User, error)
	ChangePassword(userID string, oldPassword string, newPassword string) error

	GetUserByID(id string) (*model.User, error)
	GetUserByUsername(username string) (*model.User, error)
	GetUserByBatch(batchSize int, page int) ([]*model.User, int, error)
	UpdateUser(userID string, request *request.UpdateUserRequest) error
	DeleteUser(user *model.User) error
	DeleteUserByID(userID string) error

	FollowUser(followerID string, followeeID string) error
	GetAllFollowees(followerID string) ([]*model.User, error)
	UnFollowUser(followerID string, followeeID string) error

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

func (s *userService) ChangePassword(userID string, oldPassword string, newPassword string) error {
	user, err := s.userRepo.GetByID(userID)
	if err != nil {
		return err
	}

	if newPassword == user.Password {
		return errors.New("new password cannot be the same as old password")
	}

	err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(oldPassword))
	if err != nil {
		return err
	}

	encryptedPassword, err := bcrypt.GenerateFromPassword([]byte(newPassword), bcrypt.DefaultCost)
	if err != nil {
		return err
	}

	user.Password = string(encryptedPassword)
	return s.userRepo.Update(user)
}

func (s *userService) GetUserByID(id string) (*model.User, error) {
	return s.userRepo.GetByID(id)
}

func (s *userService) GetUserByUsername(username string) (*model.User, error) {
	return s.userRepo.GetByUsername(username)
}

func (s *userService) GetUserByBatch(batchSize int, page int) ([]*model.User, int, error) {
	if batchSize < 10 || batchSize > 1000 {
		return nil, 0, errors.New("batch size too large or too small")
	}

	if page < 1 {
		return nil, 0, errors.New("page must be greater than zero")
	}

	return s.userRepo.GetUsersByBatch(batchSize, page)
}

func (s *userService) UpdateUser(userID string, request *request.UpdateUserRequest) error {
	user, err := s.userRepo.GetByID(userID)
	if err != nil {
		return err
	}

	isChange := false

	if request.Username != nil {
		user.Username = *request.Username
		isChange = true
	}

	if request.Email != nil {
		user.Email = *request.Email
		isChange = true
	}

	if request.Phone != nil {
		user.Phone = *request.Phone
		isChange = true
	}

	if request.FullName != nil {
		user.Fullname = *request.FullName
		isChange = true
	}

	if request.CitizenId != nil {
		user.CitizenId = *request.CitizenId
		isChange = true
	}

	if !isChange {
		return errors.New("no change")
	} else {
		now := time.Now()
		user.UpdatedAt = &now
	}

	return s.userRepo.Update(user)
}

func (s *userService) DeleteUser(user *model.User) error {
	return s.userRepo.Delete(user)
}

func (s *userService) DeleteUserByID(ID string) error {
	return s.userRepo.DeleteByID(ID)
}

func (s *userService) FollowUser(followerID string, followeeID string) error {
	_, err := s.userRepo.GetByID(followeeID)
	if errors.Is(err, gorm.ErrRecordNotFound) {
		return errors.New("user to follow not found")
	} else if err != nil {
		return err
	}

	return s.userRepo.FollowUser(&followerID, &followeeID)
}

func (s *userService) GetAllFollowees(followerID string) ([]*model.User, error) {
	return s.userRepo.GetAllFollowUser(&followerID)
}

func (s *userService) UnFollowUser(followerID string, followeeID string) error {
	return s.userRepo.UnFollowUser(&followerID, &followeeID)
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
	user.Status = model.BannedStatus

	return s.userRepo.Update(user)
}

func (s *userService) UnBanUser(userID string) error {
	user, err := s.userRepo.GetByID(userID)
	if err != nil {
		return err
	}

	user.BanStart = nil
	user.BanEnd = nil
	user.Status = model.ActiveStatus

	return s.userRepo.Update(user)
}
