package model

type Province struct {
	ID   string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Name string `json:"name"`

	Districts []District `json:"districts,omitempty"`
}

type District struct {
	ID         string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Name       string  `json:"name"`
	ProvinceID *string `gorm:"type:uuid" json:"province_id"`

	Province *Province `json:"province,omitempty"`
	Wards    []Ward    `json:"wards,omitempty"`
}

type Ward struct {
	ID         string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Name       string  `json:"name"`
	DistrictID *string `gorm:"type:uuid" json:"district_id"`

	District *District `json:"district,omitempty"`
}

type Address struct {
	ID        string  `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserID    *string `gorm:"type:uuid" json:"user_id"`
	Fullname  string  `gorm:"type:varchar(255)" json:"fullname"`
	Phone     string  `gorm:"type:varchar(10)" json:"phone"`
	WardID    *string `gorm:"type:uuid" json:"ward_id"`
	Detail    string  `json:"detail"`
	IsDefault bool    `json:"is_default"`

	User *User `json:"user,omitempty"`
	Ward *Ward `json:"ward,omitempty"`
}
