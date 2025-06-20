package model

import "time"

type CommunityStatus string
type CommunityType string
type CommunityRole string

const (
	CommunityStatusApproved CommunityStatus = "approved"
	CommunityStatusRejected CommunityStatus = "rejected"
)

const (
	CommunityTypePublic  CommunityType = "public"
	CommunityTypePrivate CommunityType = "private"
)

const (
	CommunityRoleOwner       CommunityRole = "owner"
	CommunityRoleParticipant CommunityRole = "participant"
)

type Community struct {
	ID          string          `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	Name        string          `json:"name"`
	Description string          `json:"description"`
	Status      CommunityStatus `json:"status"`
	Type        CommunityType   `json:"type"`
	CreatedAt   time.Time       `json:"created_at"`
}

type CommunityParticipant struct {
	UserID      string        `gorm:"type:uuid;primaryKey" json:"user_id"`
	CommunityID string        `gorm:"type:uuid;primaryKey" json:"community_id"`
	Role        CommunityRole `json:"role"`
	CreatedAt   time.Time     `json:"created_at"`

	User      *User      `json:"user"`
	Community *Community `json:"community"`
}
