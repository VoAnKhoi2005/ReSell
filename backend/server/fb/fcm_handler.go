package fb

import (
	"context"
	"firebase.google.com/go/messaging"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"net/http"
	"time"
)

type FCMHandler struct {
	notificationRepo repository.NotificationRepository
	userTokens       map[string]string
}

func NewFCMHandler(notificationRepo repository.NotificationRepository) *FCMHandler {
	return &FCMHandler{
		notificationRepo: notificationRepo,
		userTokens:       make(map[string]string),
	}
}

type SaveFCMTokenRequest struct {
	Token string `json:"token"`
}

func (h *FCMHandler) SaveFCMToken(c *gin.Context) {
	var request SaveFCMTokenRequest
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
		return
	}

	h.userTokens[userID] = request.Token
	err = h.HandleUnsentNotification(userID, request.Token)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, true)
}

func (h *FCMHandler) DeleteFCMToken(c *gin.Context) {
	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
		return
	}

	delete(h.userTokens, userID)

	c.JSON(http.StatusOK, true)
}

func (h *FCMHandler) SendNotification(
	userID string,
	title string,
	description string,
	isSilent bool,
	nofType model.NotificationType,
) error {
	notification := &model.Notification{
		UserID:      &userID,
		Title:       title,
		Description: &description,
		IsSilent:    isSilent,
		Type:        nofType,
		CreatedAt:   time.Now().UTC(),
		IsSent:      false,
	}

	notification, err := h.notificationRepo.Create(notification)
	if err != nil {
		return err
	}

	token, found := h.userTokens[userID]
	if !found || token == "" {
		return fmt.Errorf("failed to send notification: FCM token not found")
	}

	err = h.SendMessageToToken(token, notification)
	if err != nil {
		return fmt.Errorf("failed to send notification: %v", err)
	}

	notification.IsSent = true
	now := time.Now().UTC()
	notification.SentAt = &now
	if err = h.notificationRepo.Update(notification); err != nil {
		return fmt.Errorf("sent but failed to store: %w", err)
	}

	return nil
}

func (h *FCMHandler) HandleUnsentNotification(userID string, token string) error {
	notifications, err := h.notificationRepo.GetUnsentNotifications(userID)
	if err != nil {
		return fmt.Errorf("failed to get unsent notifications: %w", err)
	}

	for _, notification := range notifications {
		err = h.SendMessageToToken(token, notification)
		if err != nil {
			return fmt.Errorf("failed to send notification: %w", err)
		}
	}
	return nil
}

func (h *FCMHandler) SendMessageToToken(token string, notification *model.Notification) error {
	message := &messaging.Message{
		Token: token,
		Notification: &messaging.Notification{
			Title: notification.Title,
			Body: func() string {
				if notification.Description != nil {
					return *notification.Description
				}
				return ""
			}(),
		},
		Data: map[string]string{
			"notification_id": notification.ID,
			"type":            string(notification.Type),
		},
	}

	// Send to FCM
	_, err := fcmClient.Send(context.Background(), message)
	if err != nil {
		return fmt.Errorf("failed to send FCM: %w", err)
	}

	return nil
}
