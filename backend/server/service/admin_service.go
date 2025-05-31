package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"golang.org/x/crypto/bcrypt"
)

type AdminService interface {
	Register(admin *model.Admin) []string
	Login(username string, password string) (*model.Admin, error)

	ChangeAdminEmail(adminID string, newEmail string) error
	ChangeAdminPassword(adminID string, oldPassword string, newPassword string) error

	GetByID(id string) (*model.Admin, error)
	GetAll() ([]*model.Admin, error)
	Delete(admin *model.Admin) error
}

type adminService struct {
	adminRepository repository.AdminRepository
}

func NewAdminService(repo repository.AdminRepository) AdminService {
	return &adminService{adminRepository: repo}
}

func (as *adminService) Register(admin *model.Admin) []string {
	var errors []string
	var err error

	_, err = as.adminRepository.GetByEmail(admin.Email)
	if err == nil {
		errors = append(errors, "email: email already taken")
	}

	_, err = as.adminRepository.GetByUsername(admin.Username)
	if err == nil {
		errors = append(errors, "username: username already taken")
	}

	if len(errors) > 0 {
		return errors
	}

	err = as.adminRepository.Create(admin)
	if err != nil {
		errors = append(errors, err.Error())
		return errors
	}

	return nil
}

func (as *adminService) Login(username string, password string) (*model.Admin, error) {
	admin, err := as.adminRepository.GetByUsername(username)
	if err != nil {
		return nil, errors.New("invalid credentials")
	}

	if bcrypt.CompareHashAndPassword([]byte(admin.Password), []byte(password)) != nil {
		return nil, errors.New("invalid credentials")
	}

	return admin, nil
}

func (as *adminService) ChangeAdminEmail(adminID string, newEmail string) error {
	admin, err := as.adminRepository.GetByID(adminID)
	if err != nil {
		return err
	}

	admin.Email = newEmail
	return as.adminRepository.Update(admin)
}

func (as *adminService) ChangeAdminPassword(adminID string, oldPassword string, newPassword string) error {
	admin, err := as.adminRepository.GetByID(adminID)
	if err != nil {
		return err
	}

	err = bcrypt.CompareHashAndPassword([]byte(admin.Password), []byte(oldPassword))
	if err != nil {
		return err
	}

	encryptedPassword, err := bcrypt.GenerateFromPassword([]byte(newPassword), bcrypt.DefaultCost)
	if err != nil {
		return err
	}

	admin.Password = string(encryptedPassword)
	return as.adminRepository.Update(admin)
}

func (as *adminService) GetByID(ID string) (*model.Admin, error) {
	return as.adminRepository.GetByID(ID)
}

func (as *adminService) GetAll() ([]*model.Admin, error) {
	return as.adminRepository.GetAll()
}

func (as *adminService) Create(admin *model.Admin) error {
	return as.adminRepository.Create(admin)
}

func (as *adminService) Delete(admin *model.Admin) error {
	return as.adminRepository.Delete(admin)
}
