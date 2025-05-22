package service

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
)

type AddressService interface {
	Create(address *model.Address) error

	GetByID(addressID string) (*model.Address, error)
	GetByUserID(userID string) (*model.Address, error)
	GetByWardID(wardID string) ([]model.Address, error)
	GetByDistrict(districtID string) ([]model.Address, error)
	GetByProvince(provinceID string) ([]model.Address, error)
}

type addressService struct {
	AddressRepository repository.AddressRepository
}

func NewAddressService(repo repository.AddressRepository) AddressService {
	return &addressService{AddressRepository: repo}
}

func (a *addressService) Create(address *model.Address) error {
	return a.AddressRepository.Create(address)
}

func (a *addressService) GetByID(addressID string) (*model.Address, error) {
	return a.AddressRepository.GetByID(addressID)
}

func (a *addressService) GetByUserID(userID string) (*model.Address, error) {
	return a.AddressRepository.GetByID(userID)
}

func (a *addressService) GetByWardID(wardID string) ([]model.Address, error) {
	addresses, err := a.AddressRepository.GetByWardID(wardID)
	return addresses, err
}

func (a *addressService) GetByProvince(provinceID string) ([]model.Address, error) {
	var districts []model.District
	districts, err := a.AddressRepository.GetDistricts(provinceID)
	if err != nil {
		return nil, err
	}

	var districtIDs []string
	for _, district := range districts {
		districtIDs = append(districtIDs, district.ID)
	}

	var wards []model.Ward
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

func (a *addressService) GetByDistrict(districtId string) ([]model.Address, error) {
	var wards []model.Ward
	wards, err := a.AddressRepository.GetWards(districtId)
	if err != nil {
		return nil, err
	}
	if len(wards) == 0 {
		return []model.Address{}, nil
	}

	var wardIDs []string
	for _, ward := range wards {
		wardIDs = append(wardIDs, ward.ID)
	}

	addresses, err := a.AddressRepository.GetByWardIDs(wardIDs)
	return addresses, err
}
