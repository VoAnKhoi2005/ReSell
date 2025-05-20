package repositories

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/utils"
	"gorm.io/gorm"
)

type AddressRepository interface {
	Create(address *models.Address) error
	Delete(address *models.Address) error
	Update(address *models.Address) error

	GetByID(addressID uint) (*models.Address, error)
	GetByWard(wardID uint) ([]models.Address, error)
	GetByDistrict(districtID uint) ([]models.Address, error)
	GetByProvince(provinceID uint) ([]models.Address, error)

	GetWards(districtID uint) ([]models.Ward, error)
	GetDistricts(provinceID uint) ([]models.District, error)

	GetAllProvinces() ([]models.Province, error)
	GetAllDistricts() ([]models.District, error)
}

type addressRepository struct {
	db *gorm.DB
}

func NewAddressRepository(db *gorm.DB) AddressRepository {
	return &addressRepository{db: db}
}

func (a *addressRepository) Create(address *models.Address) error {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Create(address).Error
}

func (a *addressRepository) Delete(address *models.Address) error {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Delete(address).Error
}

func (a *addressRepository) Update(address *models.Address) error {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Save(address).Error
}

func (a *addressRepository) GetByID(addressID uint) (*models.Address, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var address models.Address
	err := a.db.WithContext(ctx).First(&address, addressID).Error
	return &address, err
}

func (a *addressRepository) GetByWard(wardId uint) ([]models.Address, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var addresses []models.Address
	err := a.db.WithContext(ctx).Find(&addresses, "WardID = ?", wardId).Error
	return addresses, err
}

func (a *addressRepository) GetByDistrict(districtId uint) ([]models.Address, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var wards []models.Ward
	wards, err := a.GetWards(districtId)
	if err != nil {
		return nil, err
	}
	if len(wards) == 0 {
		return []models.Address{}, nil
	}

	var wardIDs []uint
	for _, ward := range wards {
		wardIDs = append(wardIDs, ward.ID)
	}

	var addresses []models.Address
	err = a.db.WithContext(ctx).Where("WardID IN ?", wardIDs).Find(&addresses).Error

	return addresses, err
}

func (a *addressRepository) GetByProvince(provinceID uint) ([]models.Address, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var districts []models.District
	districts, err := a.GetDistricts(provinceID)
	if err != nil {
		return nil, err
	}

	var districtIDs []uint
	for _, district := range districts {
		districtIDs = append(districtIDs, district.ID)
	}

	var wards []models.Ward
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

	var addresses []models.Address
	err = a.db.WithContext(ctx).Where("WardID IN ?", wardIDs).Find(&addresses).Error

	return addresses, err
}

func (a *addressRepository) GetWards(districtID uint) ([]models.Ward, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var wards []models.Ward
	err := a.db.WithContext(ctx).Find(&wards, "DistrictID = ?", districtID).Error

	return wards, err
}

func (a *addressRepository) GetDistricts(provinceID uint) ([]models.District, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var districts []models.District
	err := a.db.WithContext(ctx).Find(&districts, "ProvinceID = ?", provinceID).Error

	return districts, err
}

func (a *addressRepository) GetAllProvinces() ([]models.Province, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var provinces []models.Province
	err := a.db.WithContext(ctx).Find(&provinces).Error

	return provinces, err
}

func (a *addressRepository) GetAllDistricts() ([]models.District, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var districts []models.District
	err := a.db.WithContext(ctx).Find(&districts).Error

	return districts, err
}
