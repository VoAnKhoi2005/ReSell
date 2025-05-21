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

	GetByID(addressID uint) (*model.Address, error)
	GetByWard(wardID uint) ([]model.Address, error)
	GetByDistrict(districtID uint) ([]model.Address, error)
	GetByProvince(provinceID uint) ([]model.Address, error)

	GetWards(districtID uint) ([]model.Ward, error)
	GetDistricts(provinceID uint) ([]model.District, error)

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

func (a *addressRepository) GetByID(addressID uint) (*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var address model.Address
	err := a.db.WithContext(ctx).First(&address, addressID).Error
	return &address, err
}

func (a *addressRepository) GetByWard(wardId uint) ([]model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []model.Address
	err := a.db.WithContext(ctx).Find(&addresses, "WardID = ?", wardId).Error
	return addresses, err
}

func (a *addressRepository) GetByDistrict(districtId uint) ([]model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var wards []model.Ward
	wards, err := a.GetWards(districtId)
	if err != nil {
		return nil, err
	}
	if len(wards) == 0 {
		return []model.Address{}, nil
	}

	var wardIDs []uint
	for _, ward := range wards {
		wardIDs = append(wardIDs, ward.ID)
	}

	var addresses []model.Address
	err = a.db.WithContext(ctx).Where("WardID IN ?", wardIDs).Find(&addresses).Error

	return addresses, err
}

func (a *addressRepository) GetByProvince(provinceID uint) ([]model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var districts []model.District
	districts, err := a.GetDistricts(provinceID)
	if err != nil {
		return nil, err
	}

	var districtIDs []uint
	for _, district := range districts {
		districtIDs = append(districtIDs, district.ID)
	}

	var wards []model.Ward
	for _, districtID := range districtIDs {
		wardsOfDistrict, err := a.GetWards(districtID)
		if err != nil {
			return nil, err
		}
		wards = append(wards, wardsOfDistrict...)
	}

	var wardIDs []uint
	for _, ward := range wards {
		wardIDs = append(wardIDs, ward.ID)
	}

	var addresses []model.Address
	err = a.db.WithContext(ctx).Where("WardID IN ?", wardIDs).Find(&addresses).Error

	return addresses, err
}

func (a *addressRepository) GetWards(districtID uint) ([]model.Ward, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var wards []model.Ward
	err := a.db.WithContext(ctx).Find(&wards, "DistrictID = ?", districtID).Error

	return wards, err
}

func (a *addressRepository) GetDistricts(provinceID uint) ([]model.District, error) {
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
