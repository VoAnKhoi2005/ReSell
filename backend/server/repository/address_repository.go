package repository

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/util"
	"gorm.io/gorm"
)

type AddressRepository interface {
	GetAll() ([]*model.Address, error)
	Create(user *model.Address) error
	Update(user *model.Address) error
	Delete(user *model.Address) error

	GetByID(addressID string) (*model.Address, error)
	GetByUserID(userID string) ([]*model.Address, error)
	GetByWardID(wardID string) ([]*model.Address, error)
	GetByWardIDs(wardIDs []string) ([]*model.Address, error)

	GetWards(districtID string) ([]*model.Ward, error)
	GetDistricts(provinceID string) ([]*model.District, error)

	GetAllProvinces() ([]*model.Province, error)
	GetAllDistricts() ([]*model.District, error)

	CreateProvince(province *model.Province) error
	CreateDistrict(district *model.District) error
	CreateWard(ward *model.Ward) error
}

type addressRepository struct {
	*BaseRepository[model.Address]
}

func NewAddressRepository(db *gorm.DB) AddressRepository {
	return &addressRepository{BaseRepository: NewBaseRepository[model.Address](db)}
}

func (a *addressRepository) GetByID(addressID string) (*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var address model.Address
	err := a.db.WithContext(ctx).First(&address, addressID).Error
	return &address, err
}

func (a *addressRepository) GetByUserID(userID string) ([]*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []*model.Address
	err := a.db.WithContext(ctx).Find(&addresses, "user_id = ?", userID).Error
	return addresses, err
}

func (a *addressRepository) GetByWardID(wardID string) ([]*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []*model.Address
	err := a.db.WithContext(ctx).Find(&addresses, "ward_id = ?", wardID).Error
	return addresses, err
}

func (a *addressRepository) GetByWardIDs(wardIDs []string) ([]*model.Address, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var addresses []*model.Address
	err := a.db.WithContext(ctx).Find(&addresses, "ward_id IN ?", wardIDs).Error
	return addresses, err
}

func (a *addressRepository) GetWards(districtID string) ([]*model.Ward, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var wards []*model.Ward
	err := a.db.WithContext(ctx).Find(&wards, "district_id = ?", districtID).Error

	return wards, err
}

func (a *addressRepository) GetDistricts(provinceID string) ([]*model.District, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var districts []*model.District
	err := a.db.WithContext(ctx).Find(&districts, "province_id = ?", provinceID).Error

	return districts, err
}

func (a *addressRepository) GetAllProvinces() ([]*model.Province, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var provinces []*model.Province
	err := a.db.WithContext(ctx).Find(&provinces).Error

	return provinces, err
}

func (a *addressRepository) GetAllDistricts() ([]*model.District, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var districts []*model.District
	err := a.db.WithContext(ctx).Find(&districts).Error

	return districts, err
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
