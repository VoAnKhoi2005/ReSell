package repository

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
	"time"
)

type NotificationRepository interface {
	GetAll() ([]*model.Notification, error)
	Create(notification *model.Notification) error
	Update(notification *model.Notification) error
	Delete(notification *model.Notification) error

	GetByID(notificationID string) (*model.Notification, error)
	GetNotificationsByBatch(userID string, batchSize int, page int) ([]*model.Notification, int, error)
	GetNotificationsByDate(userID string, date time.Time) ([]*model.Notification, error)
	GetNotificationsByType(userID string, notificationType model.NotificationType) ([]*model.Notification, error)
}

type notificationRepository struct {
	*BaseRepository[model.Notification]
}

func NewNotificationRepository(db *gorm.DB) NotificationRepository {
	return &notificationRepository{BaseRepository: NewBaseRepository[model.Notification](db)}
}

func (n *notificationRepository) GetByID(notificationID string) (*model.Notification, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var notification *model.Notification = nil
	err := n.db.WithContext(ctx).First(&notification, "id = ?", notificationID).Error
	return notification, err
}

func (n *notificationRepository) GetNotificationsByBatch(userID string, batchSize int, page int) ([]*model.Notification, int, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var totalCount int64
	err := n.db.WithContext(ctx).Model(&model.Notification{}).Where("user_id = ?", userID).Count(&totalCount).Error
	if err != nil {
		return nil, 0, err
	}

	totalBatches := int((totalCount + int64(batchSize) - 1) / int64(batchSize))
	offset := (page - 1) * batchSize

	if totalBatches == 0 {
		totalBatches = 1
	}

	if page > totalBatches {
		return nil, totalBatches, fmt.Errorf("page %d out of range: total pages %d", page, totalBatches)
	}

	var notifications []*model.Notification
	err = n.db.WithContext(ctx).
		Where("user_id = ?", userID).
		Order("created_at DESC").
		Limit(batchSize).Offset(offset).
		Find(&notifications).Error

	if err != nil {
		return nil, 0, err
	}

	return notifications, totalBatches, err
}

func (n *notificationRepository) GetNotificationsByDate(userID string, date time.Time) ([]*model.Notification, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	// Set range: beginning to end of that date
	startOfDay := time.Date(date.Year(), date.Month(), date.Day(), 0, 0, 0, 0, time.UTC)
	endOfDay := startOfDay.Add(24 * time.Hour)

	var notifications []*model.Notification
	err := n.db.WithContext(ctx).
		Where("user_id = ? AND created_at >= ? AND created_at < ?", userID, startOfDay, endOfDay).
		Order("created_at DESC").
		Find(&notifications).Error

	return notifications, err
}

func (n *notificationRepository) GetNotificationsByType(userID string, notificationType model.NotificationType) ([]*model.Notification, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var notifications []*model.Notification
	err := n.db.WithContext(ctx).
		Where("user_id = ? AND type = ?", userID, notificationType).
		Order("created_at DESC").
		Find(&notifications).Error

	return notifications, err
}
