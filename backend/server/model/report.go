package model

type ReportUser struct {
	ID          string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	ReporterID  string `gorm:"type:uuid" json:"reporter_id"`
	ReportedID  string `gorm:"type:uuid" json:"reported_id"`
	Description string `gorm:"type:text" json:"description"`

	Reporter *User `json:"reporter"`
	Reported *User `json:"reported"`
}

type ReportPost struct {
	ID          string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	ReporterID  string `gorm:"type:uuid" json:"reporter_id"`
	ReportedID  string `gorm:"type:uuid" json:"reported_id"`
	Description string `gorm:"type:text" json:"description"`

	Reporter *User `json:"reporter"`
	Reported *User `json:"reported"`
}
