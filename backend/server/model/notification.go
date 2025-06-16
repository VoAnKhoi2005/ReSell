package model

import "time"

type NotificationType string

const (
	TypeMessage  NotificationType = "message"
	TypeAlert    NotificationType = "alert"
	TypeSystem   NotificationType = "system"
	TypeReminder NotificationType = "reminder"
)

type Notification struct {
	ID          string            `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserID      *string           `gorm:"type:uuid" json:"user_id"`
	Title       string            `json:"title"`
	Description *string           `json:"description,omitempty"`
	Topic       *string           `json:"topic,omitempty"` // FCM topic (if broadcast)
	Data        map[string]string `json:"data,omitempty"`  // Custom key-value data
	Type        NotificationType  `json:"type"`            // Purpose of notification
	IsRead      bool              `json:"is_read"`
	IsSent      bool              `json:"is_sent"` // Was sent to FCM
	CreatedAt   time.Time         `json:"created_at"`
	SentAt      *time.Time        `json:"sent_at"`

	User *User `json:"user,omitempty"`
}
