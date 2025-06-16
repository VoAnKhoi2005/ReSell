package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"time"
)

type NotificationService interface {
	CreateNotification(notification *model.Notification) error
	DeleteNotification(notification string) error

	GetByID(notificationID string) (*model.Notification, error)
	GetNotificationsByBatch(userID string, batchSize int, page int) ([]*model.Notification, int, error)
	GetNotificationsByDate(userID string, date time.Time) ([]*model.Notification, error)
	GetNotificationsByType(userID string, notificationType model.NotificationType) ([]*model.Notification, error)
}

type notificationService struct {
	notificationRepository repository.NotificationRepository
}

func NewNotificationService(notificationRepo repository.NotificationRepository) NotificationService {
	return &notificationService{notificationRepository: notificationRepo}
}

func (n *notificationService) CreateNotification(notification *model.Notification) error {
	return n.notificationRepository.Create(notification)
}

func (n *notificationService) DeleteNotification(notificationID string) error {
	notification, err := n.notificationRepository.GetByID(notificationID)
	if err != nil {
		return err
	}

	return n.notificationRepository.Delete(notification)
}

func (n *notificationService) GetByID(notificationID string) (*model.Notification, error) {
	return n.notificationRepository.GetByID(notificationID)
}

func (n *notificationService) GetNotificationsByBatch(userID string, batchSize int, page int) ([]*model.Notification, int, error) {
	if batchSize < 10 || batchSize > 1000 {
		return nil, 0, errors.New("batch size too large or too small")
	}

	if page < 1 {
		return nil, 0, errors.New("page must be greater than zero")
	}

	return n.notificationRepository.GetNotificationsByBatch(userID, batchSize, page)
}

func (n *notificationService) GetNotificationsByDate(userID string, date time.Time) ([]*model.Notification, error) {
	return n.notificationRepository.GetNotificationsByDate(userID, date)
}

func (n *notificationService) GetNotificationsByType(userID string, notificationType model.NotificationType) ([]*model.Notification, error) {
	return n.notificationRepository.GetNotificationsByType(userID, notificationType)
}
