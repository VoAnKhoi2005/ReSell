package model

type Province struct {
	ID   string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	Name string

	Districts []District
}

type District struct {
	ID         string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	Name       string
	ProvinceID string `gorm:"type:uuid"`

	Province *Province
	Wards    []Ward
}

type Ward struct {
	ID         string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	Name       string
	DistrictID string `gorm:"type:uuid"`

	District *District
}

type Address struct {
	ID        string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()"`
	UserID    string `gorm:"type:uuid"`
	WardID    string `gorm:"type:uuid"`
	Detail    string
	IsDefault bool

	User *User
	Ward *Ward
}
