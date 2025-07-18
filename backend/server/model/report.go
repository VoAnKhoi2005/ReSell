package model

import "time"

type ReportUser struct {
	ID          string    `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	ReporterID  string    `gorm:"type:uuid" json:"reporter_id"`
	ReportedID  string    `gorm:"type:uuid" json:"reported_id"`
	Description string    `gorm:"type:text" json:"description"`
	CreatedAt   time.Time `json:"created_at"`

	Reporter *User `json:"reporter,omitempty"`
	Reported *User `json:"reported,omitempty"`
}

type ReportPost struct {
	ID          string `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	ReporterID  string `gorm:"type:uuid" json:"reporter_id"`
	ReportedID  string `gorm:"type:uuid" json:"reported_id"`
	Description string `gorm:"type:text" json:"description"`

	Reporter  *User     `json:"reporter,omitempty"`
	Reported  *Post     `json:"reported,omitempty"`
	CreatedAt time.Time `json:"created_at"`
}
