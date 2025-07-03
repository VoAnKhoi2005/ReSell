package data

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/google/uuid"
	"math/rand"
	"time"
)

func seedProvince() []string {
	provinceNames := []string{"Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ"}
	var provinces []model.Province
	var provinceIDs []string

	for _, name := range provinceNames {
		id := uuid.New().String()
		p := model.Province{
			ID:   id,
			Name: name,
		}
		provinces = append(provinces, p)
		provinceIDs = append(provinceIDs, id)
	}

	config.DB.Create(&provinces)
	return provinceIDs
}

func seedDistrict(provinceIDs []string) []string {
	// Danh sách 5 quận cho mỗi tỉnh
	districtNames := [][]string{
		{"Ba Đình", "Hoàn Kiếm", "Đống Đa", "Cầu Giấy", "Thanh Xuân"},      // Hà Nội
		{"Quận 1", "Quận 3", "Quận 5", "Quận 10", "Phú Nhuận"},             // TP.HCM
		{"Hải Châu", "Thanh Khê", "Sơn Trà", "Ngũ Hành Sơn", "Liên Chiểu"}, // Đà Nẵng
		{"Hồng Bàng", "Ngô Quyền", "Lê Chân", "Hải An", "Kiến An"},         // Hải Phòng
		{"Ninh Kiều", "Bình Thủy", "Cái Răng", "Ô Môn", "Thốt Nốt"},        // Cần Thơ
	}

	var districts []model.District
	var districtIDs []string

	for i, provinceID := range provinceIDs {
		for _, name := range districtNames[i] {
			id := uuid.New().String()
			d := model.District{
				ID:         id,
				Name:       name,
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
	// 5 phường đại diện, dùng chung cho tất cả quận (đơn giản hóa)
	wardNames := []string{"Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5"}

	var wards []model.Ward
	var wardIDs []string

	for _, districtID := range districtIDs {
		for _, name := range wardNames {
			id := uuid.New().String()
			w := model.Ward{
				ID:         id,
				Name:       name,
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

	rand.Seed(time.Now().UnixNano())

	for i, userID := range userIDs {
		id := uuid.New().String()
		wardID := wardIDs[rand.Intn(len(wardIDs))]

		address := model.Address{
			ID:        id,
			UserID:    &userID,
			WardID:    &wardID,
			Detail:    fmt.Sprintf("Số nhà %d, Đường ABC", i+1),
			IsDefault: true,
		}

		addresses = append(addresses, address)
		addressIDs = append(addressIDs, id)
	}

	config.DB.Create(&addresses)
	return addressIDs
}
