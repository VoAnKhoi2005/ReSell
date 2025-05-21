package service

import (
	"github.com/VoAnKhoi2005/ReSell/models"
	"github.com/VoAnKhoi2005/ReSell/repositories"
)

type AddressService interface {
	GetByID(addressID uint) (*models.Address, error)
	GetByUserID(userID uint) (*models.Address, error)
	GetByWard(wardID uint) ([]models.Address, error)
	GetByDistrict(districtID uint) ([]models.Address, error)
	GetByProvince(provinceID uint) ([]models.Address, error)
}

type addressService struct {
	AddressRepository repositories.AddressRepository
}

func NewAddressService(repo repositories.AddressRepository) AddressService {
	return &addressService{AddressRepository: repo}
}

func (a *addressService) GetByID(addressID uint) (*models.Address, error) {
	return a.AddressRepository.GetByID(addressID)
}

func (a *addressService) GetByUserID(userID uint) (*models.Address, error) {
	return a.AddressRepository.GetByID(userID)
}

func (a *addressService) GetByWard(wardId uint) ([]models.Address, error) {
	addresses, err := a.AddressRepository.GetByWardID(wardId)
	return addresses, err
}

func (a *addressService) GetByProvince(provinceID uint) ([]models.Address, error) {
	var districts []models.District
	districts, err := a.AddressRepository.GetDistricts(provinceID)
	if err != nil {
		return nil, err
	}

	var districtIDs []uint
	for _, district := range districts {
		districtIDs = append(districtIDs, district.ID)
	}

	var wards []models.Ward
	for _, districtID := range districtIDs {
		wardsOfDistrict, err := a.AddressRepository.GetWards(districtID)
		if err != nil {
			return nil, err
		}
		wards = append(wards, wardsOfDistrict...)
	}

	var wardIDs []uint
	for _, ward := range wards {
		wardIDs = append(wardIDs, ward.ID)
	}

	addresses, err := a.AddressRepository.GetByWardIDs(wardIDs)
	return addresses, err
}

func (a *addressService) GetByDistrict(districtId uint) ([]models.Address, error) {
	var wards []models.Ward
	wards, err := a.AddressRepository.GetWards(districtId)
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

	addresses, err := a.AddressRepository.GetByWardIDs(wardIDs)
	return addresses, err
}
