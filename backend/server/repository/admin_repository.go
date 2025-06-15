package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type AdminRepository interface {
	GetAll() ([]*model.Admin, error)
	Create(admin *model.Admin) error
	Update(admin *model.Admin) error
	Delete(admin *model.Admin) error

	GetByID(ID string) (*model.Admin, error)
	GetByUsername(username string) (*model.Admin, error)
	GetByEmail(email string) (*model.Admin, error)
}

type adminRepository struct {
	*BaseRepository[model.Admin]
}

func NewAdminRepository(db *gorm.DB) AdminRepository {
	return &adminRepository{
		BaseRepository: NewBaseRepository[model.Admin](db),
	}
}

func (a *adminRepository) GetByID(ID string) (*model.Admin, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var admin *model.Admin = nil
	err := a.db.WithContext(ctx).First(&admin, "id  = ?", ID).Error
	return admin, err
}

func (a *adminRepository) GetByUsername(username string) (*model.Admin, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var admin *model.Admin = nil
	err := a.db.WithContext(ctx).First(&admin, "username = ?", username).Error
	return admin, err
}

func (a *adminRepository) GetByEmail(email string) (*model.Admin, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var admin *model.Admin = nil
	err := a.db.WithContext(ctx).First(&admin, "email = ?", email).Error
	return admin, err
}
