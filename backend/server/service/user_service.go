package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	request "github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
	"time"
)

type UserService interface {
	Register(user *model.User) map[string]string
	Login(identifier string, password string, loginType string) (*model.User, error)
	ChangePassword(userID string, oldPassword string, newPassword string) error

	GetUserByID(id string) (*model.User, error)
	GetUserByUsername(username string) (*model.User, error)
	GetUserByEmail(email string) (*model.User, error)
	GetUserByPhone(phone string) (*model.User, error)
	GetUserByFirebaseUID(firebaseUID string) (*model.User, error)
	GetUserByBatch(batchSize int, page int) ([]*model.User, int, error)

	IsUsernameExist(username string) (bool, error)
	IsEmailExist(email string) (bool, error)
	IsPhoneExist(phone string) (bool, error)

	UpdateUser(userID string, request *request.UpdateUserRequest) (*model.User, error)
	DeleteUser(user *model.User) error
	DeleteUserByID(userID string) error

	FollowUser(followerID string, followeeID string) error
	GetAllFollowees(followerID string) ([]*model.User, error)
	UnFollowUser(followerID string, followeeID string) error

	BanUserForDay(userID string, length uint) error
	UnBanUser(userID string) error
	SetAvatar(id string, url string) error
	SetCover(id string, url string) error

	GetStat(userID string) (*dto.UserStatDTO, error)
	UpdateReputation(userID string, reputation int) error
	SearchUsername(query string) ([]*model.User, error)
}

type userService struct {
	userRepository repository.UserRepository
}

func (s *userService) SetAvatar(id string, url string) error {
	user, err := s.userRepository.GetByID(id)
	if err != nil {
		return err
	}
	user.AvatarURL = &url
	return s.userRepository.Update(user)

}

func (s *userService) SetCover(id string, url string) error {
	user, err := s.userRepository.GetByID(id)
	if err != nil {
		return err
	}
	user.CoverURL = &url
	return s.userRepository.Update(user)
}

func (s *userService) UpdateReputation(userID string, reputation int) error {
	user, err := s.userRepository.GetByID(userID)
	if err != nil {
		return err
	}

	user.Reputation = reputation
	return s.userRepository.Update(user)
}

func NewUserService(repo repository.UserRepository) UserService {
	return &userService{userRepository: repo}
}

func (s *userService) Register(user *model.User) map[string]string {
	var validationErrors map[string]string
	validationErrors = make(map[string]string)

	if user.Email != nil {
		_, err := s.userRepository.GetByEmail(*user.Email)
		if err == nil {
			validationErrors["EmailAlreadyExists"] = "Email Already Exists"
		}
	}

	if user.Phone != nil {
		_, err := s.userRepository.GetByEmail(*user.Phone)
		if err == nil {
			validationErrors["phone"] = "phone number already taken"
		}
	}

	if _, err := s.userRepository.GetByUsername(user.Username); err == nil {
		validationErrors["username"] = "username already taken"
	}

	if len(validationErrors) > 0 {
		return validationErrors
	}

	err := s.userRepository.Create(user)
	if err != nil {
		validationErrors["system"] = "internal server error: " + err.Error()
		return validationErrors
	}

	return nil
}

func (s *userService) Login(identifier string, password string, loginType string) (*model.User, error) {
	var user *model.User
	var err error
	switch loginType {
	case "email":
		user, err = s.userRepository.GetByEmail(identifier)
	case "phone":
		user, err = s.userRepository.GetByPhone(identifier)
	case "username":
		user, err = s.userRepository.GetByUsername(identifier)
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
	user, err := s.userRepository.GetByID(userID)
	if err != nil {
		return err
	}

	if user.AuthProvider != model.PhoneAuth {
		return errors.New("cannot change password for google auth user")
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
	encryptedPasswordStr := string(encryptedPassword)
	user.Password = encryptedPasswordStr
	return s.userRepository.Update(user)
}

func (s *userService) GetUserByID(id string) (*model.User, error) {
	return s.userRepository.GetByID(id)
}

func (s *userService) GetUserByUsername(username string) (*model.User, error) {
	return s.userRepository.GetByUsername(username)
}

func (s *userService) GetUserByEmail(email string) (*model.User, error) {
	return s.userRepository.GetByEmail(email)
}

func (s *userService) GetUserByPhone(phone string) (*model.User, error) {
	return s.userRepository.GetByPhone(phone)
}

func (s *userService) GetUserByFirebaseUID(firebaseUID string) (*model.User, error) {
	return s.userRepository.GetByFirebaseUID(firebaseUID)
}

func (s *userService) GetUserByBatch(batchSize int, page int) ([]*model.User, int, error) {
	if batchSize < 10 || batchSize > 1000 {
		return nil, 0, errors.New("batch size too large or too small")
	}

	if page < 1 {
		return nil, 0, errors.New("page must be greater than zero")
	}

	return s.userRepository.GetUsersByBatch(batchSize, page)
}

func (s *userService) IsUsernameExist(username string) (bool, error) {
	user, err := s.userRepository.GetByUsername(username)
	return user != nil, err
}

func (s *userService) IsEmailExist(email string) (bool, error) {
	user, err := s.userRepository.GetByEmail(email)
	return user != nil, err
}

func (s *userService) IsPhoneExist(phone string) (bool, error) {
	user, err := s.userRepository.GetByPhone(phone)
	return user != nil, err
}

func (s *userService) UpdateUser(userID string, request *request.UpdateUserRequest) (*model.User, error) {
	user, err := s.userRepository.GetByID(userID)
	if err != nil {
		return nil, err
	}

	if request.FullName != nil && *request.FullName != "" {
		user.Fullname = *request.FullName
	}

	if request.Email != nil && *request.Email != "" {
		user.Email = request.Email
		user.IsEmailVerified = true
	}

	if request.Phone != nil && *request.Phone != "" {
		user.Phone = request.Phone
		user.IsPhoneVerified = true
	}

	err = s.userRepository.Update(user)
	if err != nil {
		return nil, err
	}

	return s.userRepository.GetByID(user.ID)
}

func (s *userService) DeleteUser(user *model.User) error {
	return s.userRepository.Delete(user)
}

func (s *userService) DeleteUserByID(ID string) error {
	return s.userRepository.DeleteByID(ID)
}

func (s *userService) FollowUser(followerID string, followeeID string) error {
	_, err := s.userRepository.GetByID(followeeID)
	if errors.Is(err, gorm.ErrRecordNotFound) {
		return errors.New("user to follow not found")
	} else if err != nil {
		return err
	}

	var follow *model.Follow
	follow.FollowerID = &followerID
	follow.FolloweeID = &followeeID

	return s.userRepository.FollowUser(follow)
}

func (s *userService) GetAllFollowees(followerID string) ([]*model.User, error) {
	return s.userRepository.GetAllFollowUser(&followerID)
}

func (s *userService) UnFollowUser(followerID string, followeeID string) error {
	return s.userRepository.UnFollowUser(&followerID, &followeeID)
}

func (s *userService) BanUserForDay(userID string, length uint) error {
	if length < 1 {
		return errors.New("invalid length")
	}

	user, err := s.userRepository.GetByID(userID)
	if err != nil {
		return err
	}

	banStart := time.Now().UTC()
	banEnd := banStart.Add(time.Duration(length) * time.Hour * 24)

	user.BanStart = &banStart
	user.BanEnd = &banEnd
	user.Status = model.BannedStatus

	return s.userRepository.Update(user)
}

func (s *userService) UnBanUser(userID string) error {
	user, err := s.userRepository.GetByID(userID)
	if err != nil {
		return err
	}

	user.BanStart = nil
	user.BanEnd = nil
	user.Status = model.ActiveStatus

	return s.userRepository.Update(user)
}

func (s *userService) GetStat(userID string) (*dto.UserStatDTO, error) {
	return s.userRepository.GetStat(userID)
}

func (s *userService) SearchUsername(query string) ([]*model.User, error) {
	return s.userRepository.SearchUsername(query)
}
