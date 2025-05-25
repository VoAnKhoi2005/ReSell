package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"gorm.io/gorm"
)

type AddressService interface {
	CreateAddress(address *model.Address) error
	UpdateAddress(address *model.Address) error
	DeleteAddress(addressID string) error

	GetByID(addressID string) (*model.Address, error)
	GetByUserID(userID string) ([]*model.Address, error)
	GetByWardID(wardID string) ([]*model.Address, error)
	GetByDistrict(districtID string) ([]*model.Address, error)
	GetByProvince(provinceID string) ([]*model.Address, error)

	GetAllProvinces() ([]*model.Province, error)
	GetDistricts(provinceID string) ([]*model.District, error)
	GetWards(provinceID string) ([]*model.Ward, error)

	CreateProvince(province *model.Province) error
	CreateDistrict(district *model.District) error
	CreateWard(ward *model.Ward) error

	UpdateProvince(provinceID string, newName string) error
	UpdateDistrict(districtID string, newName string) error
	UpdateWard(WardID string, newName string) error

	DeleteProvinces() []error
	DeleteProvince(provinceID string) error

	DeleteDistricts(provinceID string) []error
	DeleteDistrict(districtID string) error

	DeleteWards(districtID string) []error
	DeleteWard(wardID string) error
}

type addressService struct {
	AddressRepository repository.AddressRepository
}

func NewAddressService(repo repository.AddressRepository) AddressService {
	return &addressService{AddressRepository: repo}
}

func (a *addressService) CreateAddress(address *model.Address) error {
	return a.AddressRepository.Create(address)
}

func (a *addressService) UpdateAddress(address *model.Address) error {
	return a.AddressRepository.Update(address)
}

func (a *addressService) DeleteAddress(addressID string) error {
	address, err := a.GetByID(addressID)
	if err != nil {
		return err
	}
	return a.AddressRepository.Delete(address)
}

func (a *addressService) GetByID(addressID string) (*model.Address, error) {
	return a.AddressRepository.GetByID(addressID)
}

func (a *addressService) GetByUserID(userID string) ([]*model.Address, error) {
	return a.AddressRepository.GetByUserID(userID)
}

func (a *addressService) GetByWardID(wardID string) ([]*model.Address, error) {
	addresses, err := a.AddressRepository.GetByWardID(wardID)
	return addresses, err
}

func (a *addressService) GetByDistrict(districtId string) ([]*model.Address, error) {
	var wards []*model.Ward
	wards, err := a.AddressRepository.GetWards(districtId)
	if err != nil {
		return nil, err
	}
	if len(wards) == 0 {
		return []*model.Address{}, nil
	}

	var wardIDs []string
	for _, ward := range wards {
		wardIDs = append(wardIDs, ward.ID)
	}

	addresses, err := a.AddressRepository.GetByWardIDs(wardIDs)
	return addresses, err
}

func (a *addressService) GetByProvince(provinceID string) ([]*model.Address, error) {
	var districts []*model.District
	districts, err := a.AddressRepository.GetDistricts(provinceID)
	if err != nil {
		return nil, err
	}

	var districtIDs []string
	for _, district := range districts {
		districtIDs = append(districtIDs, district.ID)
	}

	var wards []*model.Ward
	for _, districtID := range districtIDs {
		wardsOfDistrict, err := a.AddressRepository.GetWards(districtID)
		if err != nil {
			return nil, err
		}
		wards = append(wards, wardsOfDistrict...)
	}

	var wardIDs []string
	for _, ward := range wards {
		wardIDs = append(wardIDs, ward.ID)
	}

	addresses, err := a.AddressRepository.GetByWardIDs(wardIDs)
	return addresses, err
}

func (a *addressService) GetAllProvinces() ([]*model.Province, error) {
	return a.AddressRepository.GetAllProvinces()
}

func (a *addressService) GetDistricts(provinceID string) ([]*model.District, error) {
	return a.AddressRepository.GetDistricts(provinceID)
}

func (a *addressService) GetWards(provinceID string) ([]*model.Ward, error) {
	return a.AddressRepository.GetWards(provinceID)
}

func (a *addressService) CreateProvince(province *model.Province) error {
	_, err := a.AddressRepository.GetProvinceByName(province.Name)
	if err != nil && !errors.Is(err, gorm.ErrRecordNotFound) {
		return err
	}
	if err == nil {
		return errors.New("province already exists")
	}

	return a.AddressRepository.CreateProvince(province)
}

func (a *addressService) CreateDistrict(district *model.District) error {
	_, err := a.AddressRepository.GetDistrictByName(district.Name)
	if err != nil && !errors.Is(err, gorm.ErrRecordNotFound) {
		return err
	}
	if err == nil {
		return errors.New("district already exists")
	}

	return a.AddressRepository.CreateDistrict(district)
}

func (a *addressService) CreateWard(ward *model.Ward) error {
	_, err := a.AddressRepository.GetWardByName(ward.Name)
	if err != nil && !errors.Is(err, gorm.ErrRecordNotFound) {
		return err
	}
	if err == nil {
		return errors.New("district already exists")
	}

	return a.AddressRepository.CreateWard(ward)
}

func (a *addressService) UpdateProvince(provinceID string, newName string) error {
	province, err := a.AddressRepository.GetProvince(provinceID)
	if err != nil {
		return err
	}

	province.Name = newName
	return a.AddressRepository.UpdateProvince(province)
}

func (a *addressService) UpdateDistrict(districtID string, newName string) error {
	district, err := a.AddressRepository.GetDistrict(districtID)
	if err != nil {
		return err
	}

	district.Name = newName
	return a.AddressRepository.UpdateDistrict(district)
}

func (a *addressService) UpdateWard(wardID string, newName string) error {
	ward, err := a.AddressRepository.GetWard(wardID)
	if err != nil {
		return err
	}

	ward.Name = newName
	return a.AddressRepository.UpdateWard(ward)
}

func (a *addressService) DeleteProvinces() []error {
	provinces, err := a.AddressRepository.GetAllProvinces()
	if err != nil {
		return []error{err}
	}

	var errs []error
	for _, province := range provinces {
		err = a.AddressRepository.DeleteProvince(province)
		if err != nil {
			errs = append(errs, err)
		}
	}

	return errs
}

func (a *addressService) DeleteProvince(provinceID string) error {
	province, err := a.AddressRepository.GetProvince(provinceID)
	if err != nil {
		return err
	}

	return a.AddressRepository.DeleteProvince(province)
}

func (a *addressService) DeleteDistricts(provinceID string) []error {
	districts, err := a.AddressRepository.GetDistricts(provinceID)
	if err != nil {
		return []error{err}
	}

	var errs []error
	for _, district := range districts {
		err = a.AddressRepository.DeleteDistrict(district)
		if err != nil {
			errs = append(errs, err)
		}
	}

	return errs
}

func (a *addressService) DeleteDistrict(districtID string) error {
	district, err := a.AddressRepository.GetDistrict(districtID)
	if err != nil {
		return err
	}

	return a.AddressRepository.DeleteDistrict(district)
}

func (a *addressService) DeleteWards(provinceID string) []error {
	wards, err := a.AddressRepository.GetWards(provinceID)
	if err != nil {
		return []error{err}
	}

	var errs []error
	for _, ward := range wards {
		err = a.AddressRepository.DeleteWard(ward)
		if err != nil {
			errs = append(errs, err)
		}
	}
	return errs
}

func (a *addressService) DeleteWard(wardID string) error {
	ward, err := a.AddressRepository.GetWard(wardID)
	if err != nil {
		return err
	}

	return a.AddressRepository.DeleteWard(ward)
}
