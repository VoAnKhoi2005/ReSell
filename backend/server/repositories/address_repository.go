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
	GetByUserID(userID uint) ([]models.Address, error)
	GetByWardID(wardID uint) ([]models.Address, error)
	GetByWardIDs(wardIDs []uint) ([]models.Address, error)

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

func (a *addressRepository) GetByUserID(userID uint) ([]models.Address, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var addresses []models.Address
	err := a.db.WithContext(ctx).Find(&addresses, "UserID = ?", userID).Error
	return addresses, err
}

func (a *addressRepository) GetByWardID(wardID uint) ([]models.Address, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var addresses []models.Address
	err := a.db.WithContext(ctx).Find(&addresses, "WardID = ?", wardID).Error
	return addresses, err
}

func (a *addressRepository) GetByWardIDs(wardIDs []uint) ([]models.Address, error) {
	ctx, cancel := utils.NewDBContext()
	defer cancel()

	var addresses []models.Address
	err := a.db.WithContext(ctx).Find(&addresses, "WardID IN ?", wardIDs).Error
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
