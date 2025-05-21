package repository

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/util"
	"gorm.io/gorm"
)

type AddressRepository interface {
	Create(address *model.Address) error
	Delete(address *model.Address) error
	Update(address *model.Address) error

	GetByID(addressID string) (*model.Address, error)
	GetByUserID(userID string) ([]model.Address, error)
	GetByWardID(wardID string) ([]model.Address, error)
	GetByWardIDs(wardIDs []string) ([]model.Address, error)

	GetWards(districtID string) ([]model.Ward, error)
	GetDistricts(provinceID string) ([]model.District, error)

	GetAllProvinces() ([]model.Province, error)
	GetAllDistricts() ([]model.District, error)
}

type addressRepository struct {
	db *gorm.DB
}

func NewAddressRepository(db *gorm.DB) AddressRepository {
	return &addressRepository{db: db}
}

func (a *addressRepository) Create(address *model.Address) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Create(address).Error
}

func (a *addressRepository) Delete(address *model.Address) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Delete(address).Error
}

func (a *addressRepository) Update(address *model.Address) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Save(address).Error
}

func (a *addressRepository) GetByID(addressID string) (*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var address model.Address
	err := a.db.WithContext(ctx).First(&address, addressID).Error
	return &address, err
}

func (a *addressRepository) GetByUserID(userID string) ([]model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []model.Address
	err := a.db.WithContext(ctx).Find(&addresses, "UserID = ?", userID).Error
	return addresses, err
}

func (a *addressRepository) GetByWardID(wardID string) ([]model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []model.Address
	err := a.db.WithContext(ctx).Find(&addresses, "WardID = ?", wardID).Error
	return addresses, err
}

func (a *addressRepository) GetByWardIDs(wardIDs []string) ([]model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []model.Address
	err := a.db.WithContext(ctx).Find(&addresses, "WardID IN ?", wardIDs).Error
	return addresses, err
}

func (a *addressRepository) GetWards(districtID string) ([]model.Ward, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var wards []model.Ward
	err := a.db.WithContext(ctx).Find(&wards, "DistrictID = ?", districtID).Error

	return wards, err
}

func (a *addressRepository) GetDistricts(provinceID string) ([]model.District, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var districts []model.District
	err := a.db.WithContext(ctx).Find(&districts, "ProvinceID = ?", provinceID).Error

	return districts, err
}

func (a *addressRepository) GetAllProvinces() ([]model.Province, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var provinces []model.Province
	err := a.db.WithContext(ctx).Find(&provinces).Error

	return provinces, err
}

func (a *addressRepository) GetAllDistricts() ([]model.District, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var districts []model.District
	err := a.db.WithContext(ctx).Find(&districts).Error

	return districts, err
}
