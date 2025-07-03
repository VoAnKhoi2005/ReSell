package data

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	lorem "github.com/drhodes/golorem"
	"github.com/google/uuid"
	"time"
)

func seedNotification(userIDs []string) {
	var notifications []model.Notification
	start := time.Now().AddDate(0, -3, 0) // 3 tháng trước
	end := time.Now()                     // hiện tại

	for _, userID := range userIDs {
		for i := 1; i <= 5; i++ {
			notification := model.Notification{
				ID:          uuid.New().String(),
				UserID:      &userID,
				Title:       lorem.Sentence(1, 10),
				Description: ptr(lorem.Paragraph(1, 2)),
				CreatedAt:   randomTimeBetween(start, end),
				Type:        model.DefaultNotification,
			}
			notifications = append(notifications, notification)
		}
	}
	config.DB.Create(&notifications)

}
