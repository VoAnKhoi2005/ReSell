package models

type Province struct {
	ID   uint
	Name string
}

type District struct {
	ID         uint
	Name       string
	ProvinceID uint

	Province *Province
}

type Ward struct {
	ID         uint
	Name       string
	DistrictID uint

	District *District
}

type Address struct {
	ID        uint
	UserID    uint
	WardID    uint
	Detail    string
	IsDefault bool

	User *User
	Ward *Ward
}
