package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type AddressRepository interface {
	GetAll() ([]*model.Address, error)
	Create(address *model.Address) error
	Update(address *model.Address) error
	Delete(address *model.Address) error
	DeleteAddresses(userID string, addressesID []string) error

	UnsetOtherDefaultAddresses(userID string) error

	GetByID(addressID string) (*model.Address, error)
	GetByUserID(userID string) ([]*model.Address, error)
	GetByWardID(wardID string) ([]*model.Address, error)
	GetByWardIDs(wardIDs []string) ([]*model.Address, error)
	GetDefaultAddress(userID string) (*model.Address, error)

	GetAllProvinces() ([]*model.Province, error)
	GetProvince(provinceID string) (*model.Province, error)
	GetProvinceByName(provinceName string) (*model.Province, error)

	GetAllDistricts() ([]*model.District, error)
	GetDistrict(districtID string) (*model.District, error)
	GetDistricts(provinceID string) ([]*model.District, error)
	GetDistrictByName(districtName string) (*model.District, error)

	GetWard(wardID string) (*model.Ward, error)
	GetWards(districtID string) ([]*model.Ward, error)
	GetWardByName(wardName string) (*model.Ward, error)

	CreateProvince(province *model.Province) error
	CreateDistrict(district *model.District) error
	CreateWard(ward *model.Ward) error

	UpdateProvince(province *model.Province) error
	UpdateDistrict(province *model.District) error
	UpdateWard(ward *model.Ward) error

	DeleteProvince(province *model.Province) error
	DeleteDistrict(district *model.District) error
	DeleteWard(ward *model.Ward) error
}

type addressRepository struct {
	*BaseRepository[model.Address]
}

func NewAddressRepository(db *gorm.DB) AddressRepository {
	return &addressRepository{BaseRepository: NewBaseRepository[model.Address](db)}
}

func (a *addressRepository) DeleteAddresses(userID string, addressesID []string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).
		Where("user_id = ? AND id IN ?", userID, addressesID).
		Delete(&model.Address{}).Error
}

func (a *addressRepository) UnsetOtherDefaultAddresses(userID string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).
		Model(&model.Address{}).
		Where("user_id = ?", userID).
		Update("is_default", false).Error
}

func (a *addressRepository) GetByID(addressID string) (*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var address *model.Address = nil
	err := a.db.WithContext(ctx).
		Preload("Ward.District.Province").
		First(&address, "id = ?", addressID).Error
	return address, err
}

func (a *addressRepository) GetByUserID(userID string) ([]*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []*model.Address
	err := a.db.WithContext(ctx).
		Preload("Ward.District.Province").
		Find(&addresses, "user_id = ?", userID).Error
	return addresses, err
}

func (a *addressRepository) GetByWardID(wardID string) ([]*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []*model.Address
	err := a.db.WithContext(ctx).
		Preload("Ward.District.Province").
		Find(&addresses, "ward_id = ?", wardID).Error
	return addresses, err
}

func (a *addressRepository) GetByWardIDs(wardIDs []string) ([]*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []*model.Address
	err := a.db.WithContext(ctx).
		Preload("Ward.District.Province").
		Find(&addresses, "ward_id IN ?", wardIDs).Error
	return addresses, err
}

func (a *addressRepository) GetDefaultAddress(userID string) (*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var address *model.Address
	err := a.db.WithContext(ctx).
		Preload("Ward.District.Province").
		First(&address, "user_id = ? AND is_default = ?", userID, true).Error
	return address, err
}

func (a *addressRepository) GetAllProvinces() ([]*model.Province, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var provinces []*model.Province
	err := a.db.WithContext(ctx).Find(&provinces).Error
	return provinces, err
}

func (a *addressRepository) GetProvince(provinceID string) (*model.Province, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var province *model.Province = nil
	err := a.db.WithContext(ctx).First(&province, "id = ?", provinceID).Error
	return province, err
}

func (a *addressRepository) GetProvinceByName(provinceName string) (*model.Province, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var province *model.Province = nil
	err := a.db.WithContext(ctx).First(&province, "name = ?", provinceName).Error
	return province, err
}

func (a *addressRepository) GetAllDistricts() ([]*model.District, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var districts []*model.District
	err := a.db.WithContext(ctx).Find(&districts).Error
	return districts, err
}

func (a *addressRepository) GetDistrict(districtID string) (*model.District, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var district *model.District = nil
	err := a.db.WithContext(ctx).First(&district, "id = ?", districtID).Error
	return district, err
}

func (a *addressRepository) GetDistricts(provinceID string) ([]*model.District, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var districts []*model.District
	err := a.db.WithContext(ctx).Find(&districts, "province_id = ?", provinceID).Error
	return districts, err
}

func (a *addressRepository) GetDistrictByName(districtName string) (*model.District, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var district *model.District = nil
	err := a.db.WithContext(ctx).First(&district, "name = ?", districtName).Error
	return district, err
}

func (a *addressRepository) GetWard(wardID string) (*model.Ward, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var ward *model.Ward = nil
	err := a.db.WithContext(ctx).First(&ward, "id = ?", wardID).Error
	return ward, err
}

func (a *addressRepository) GetWards(districtID string) ([]*model.Ward, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var wards []*model.Ward
	err := a.db.WithContext(ctx).Find(&wards, "district_id = ?", districtID).Error
	return wards, err
}

func (a *addressRepository) GetWardByName(wardName string) (*model.Ward, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var ward *model.Ward = nil
	err := a.db.WithContext(ctx).First(&ward, "name = ?", wardName).Error
	return ward, err
}

func (a *addressRepository) CreateProvince(province *model.Province) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Create(&province).Error
}

func (a *addressRepository) CreateDistrict(district *model.District) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Create(&district).Error
}

func (a *addressRepository) CreateWard(ward *model.Ward) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Create(&ward).Error
}

func (a *addressRepository) UpdateProvince(province *model.Province) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Updates(province).Error
}

func (a *addressRepository) UpdateDistrict(district *model.District) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Updates(district).Error
}

func (a *addressRepository) UpdateWard(ward *model.Ward) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Updates(ward).Error
}

func (a *addressRepository) DeleteProvince(province *model.Province) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Delete(&province).Error
}

func (a *addressRepository) DeleteDistrict(district *model.District) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Delete(&district).Error
}

func (a *addressRepository) DeleteWard(ward *model.Ward) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return a.db.WithContext(ctx).Delete(&ward).Error
}
