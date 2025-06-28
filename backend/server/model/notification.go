package model

import (
	"fmt"
	"time"
)

type NotificationType string

const (
	MessageNotification NotificationType = "message"
	AlertNotification   NotificationType = "alert"
	SystemNotification  NotificationType = "system"
	OrderNotification   NotificationType = "order"
	DefaultNotification NotificationType = "default"
)

func NotificationTypeFromString(s string) (NotificationType, error) {
	switch s {
	case "message":
		return MessageNotification, nil
	case "alert":
		return AlertNotification, nil
	case "system":
		return SystemNotification, nil
	case "order":
		return OrderNotification, nil
	default:
		return "", fmt.Errorf("invalid notification type: %s", s)
	}
}

func DefaultNotificationContent(nType NotificationType) (title string, description string) {
	switch nType {
	case MessageNotification:
		return "New Message", "You have received a new message."
	case AlertNotification:
		return "Security Alert", "Suspicious activity detected. Please review immediately."
	case SystemNotification:
		return "Maintenance Notice", "Scheduled maintenance is coming up. Service may be affected."
	case OrderNotification:
		return "Order Delivering", "Your order is coming. Be ready for shipper phone call."
	default:
		return "Notification", "You have a new notification."
	}
}

type Notification struct {
	ID          string           `gorm:"type:uuid;primary_key;default:uuid_generate_v4()" json:"id"`
	UserID      *string          `gorm:"type:uuid" json:"user_id"`
	Title       string           `json:"title"`
	Description *string          `json:"description,omitempty"`
	Topic       *string          `json:"topic,omitempty"` // FCM topic (if broadcast)
	Type        NotificationType `json:"type"`            // Purpose of notification
	IsRead      bool             `json:"is_read"`
	IsSent      bool             `json:"is_sent"` // Was sent to FCM
	IsSilent    bool             `json:"is_silent"`
	CreatedAt   time.Time        `json:"created_at"`
	SentAt      *time.Time       `json:"sent_at,omitempty"`

	User *User `json:"user,omitempty"`
}
