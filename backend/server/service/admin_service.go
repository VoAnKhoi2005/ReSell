package service

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
)

type AdminService interface {
	GetByID(id string) (*model.Admin, error)
	GetByUsername(username string) (*model.Admin, error)
	GetByEmail(email string) (*model.Admin, error)
	GetAll() ([]*model.Admin, error)
	Create(admin *model.Admin) error
	Delete(admin *model.Admin) error
}

type adminService struct {
	AdminRepository repository.AdminRepository
}

func NewAdminService(repo repository.AdminRepository) AdminService {
	return &adminService{AdminRepository: repo}
}

func (as *adminService) GetByID(ID string) (*model.Admin, error) {
	return as.AdminRepository.GetByID(ID)
}

func (as *adminService) GetByUsername(username string) (*model.Admin, error) {
	return as.AdminRepository.GetByUsername(username)
}

func (as *adminService) GetByEmail(email string) (*model.Admin, error) {
	return as.AdminRepository.GetByEmail(email)
}

func (as *adminService) GetAll() ([]*model.Admin, error) {
	return as.AdminRepository.GetAll()
}

func (as *adminService) Create(admin *model.Admin) error {
	return as.AdminRepository.Create(admin)
}

func (as *adminService) Delete(admin *model.Admin) error {
	return as.AdminRepository.Delete(admin)
}
