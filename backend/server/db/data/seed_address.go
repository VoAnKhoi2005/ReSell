package data

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/google/uuid"
)

func seedProvince() []string {
	var provinces []model.Province
	var provinceIDs []string
	for i := 1; i <= 10; i++ {
		id := uuid.New().String()
		p := model.Province{
			ID:   id,
			Name: fmt.Sprintf("Province %d", i),
		}
		provinces = append(provinces, p)
		provinceIDs = append(provinceIDs, id)

	}
	config.DB.Create(&provinces)
	return provinceIDs
}

func seedDistrict(provinceIDs []string) []string {
	var districts []model.District
	var districtIDs []string
	for i, provinceID := range provinceIDs {
		for j := 1; j <= 5; j++ {
			id := uuid.New().String()
			d := model.District{
				ID:         id,
				Name:       fmt.Sprintf("District %02d-%02d", i+1, j),
				ProvinceID: &provinceID,
			}

			districts = append(districts, d)
			districtIDs = append(districtIDs, id)
		}
	}
	config.DB.Create(&districts)
	return districtIDs
}

func seedWard(districtIDs []string) []string {
	var wards []model.Ward
	var wardIDs []string
	for i, districtID := range districtIDs {
		for j := 1; j <= 5; j++ {
			id := uuid.New().String()
			w := model.Ward{
				ID:         id,
				Name:       fmt.Sprintf("Ward %02d-%02d", i+1, j),
				DistrictID: &districtID,
			}
			wards = append(wards, w)
			wardIDs = append(wardIDs, id)
		}
	}
	config.DB.Create(&wards)
	return wardIDs
}

// Voi moi user tao mot address co wardid ngau nhien
func seedAddress(userIDs, wardIDs []string) []string {
	var addresses []model.Address
	var addressIDs []string

	for i, userID := range userIDs {
		id := uuid.New().String()
		wardID := randomStringIn(wardIDs)

		address := model.Address{
			ID:        id,
			UserID:    &userID,
			WardID:    &wardID,
			Detail:    fmt.Sprintf("Street %d", i+1),
			IsDefault: true,
		}

		addresses = append(addresses, address)
		addressIDs = append(addressIDs, id)
	}

	config.DB.Create(&addresses)
	return addressIDs
}
