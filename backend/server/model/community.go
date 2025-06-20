package model

import "time"

type CommunityStatus string
type CommunityType string
type ParticipantRole string

type ParticipantStatus string

const (
	CommunityStatusActive CommunityStatus = "active"
	CommunityStatusBanned CommunityStatus = "banned"
)

const (
	CommunityTypePublic  CommunityType = "public"
	CommunityTypePrivate CommunityType = "private"
)

const (
	ParticipantRoleOwner       ParticipantRole = "owner"
	ParticipantRoleParticipant ParticipantRole = "participant"
)

const (
	ParticipantStatusApproved ParticipantStatus = "approved"
	ParticipantStatusRejected ParticipantStatus = "rejected"
	ParticipantStatusPending  ParticipantStatus = "pending"
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
	ID          string            `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserID      string            `gorm:"type:uuid;primaryKey" json:"user_id"`
	CommunityID string            `gorm:"type:uuid;primaryKey" json:"community_id"`
	Role        ParticipantRole   `json:"role"`
	Status      ParticipantStatus `json:"status"`
	CreatedAt   time.Time         `json:"created_at"`

	User      *User      `json:"user"`
	Community *Community `json:"community"`
}
