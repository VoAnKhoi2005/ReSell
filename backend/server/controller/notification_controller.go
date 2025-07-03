package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"net/http"
	"strconv"
	"strings"
	"time"
)

type NotificationController struct {
	notificationService service.NotificationService
}

func NewNotificationController(notificationService service.NotificationService) *NotificationController {
	return &NotificationController{
		notificationService: notificationService,
	}
}

func (n *NotificationController) GetNotificationsByBatch(c *gin.Context) {
	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
		return
	}

	batchSizeStr := c.Param("batch_size")
	batchSize, err := strconv.Atoi(batchSizeStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "batch_size must be a valid integer"})
		return
	}

	pageStr := c.Param("page")
	page, err := strconv.Atoi(pageStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "page must be a valid integer"})
		return
	}

	notifications, totalBatchCount, err := n.notificationService.GetNotificationsByBatch(userID, batchSize, page)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"notifications": notifications, "total_batch_count": totalBatchCount})
}

func (n *NotificationController) GetNotificationsByDate(c *gin.Context) {
	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
		return
	}

	dateStr := c.Param("date")
	date, err := time.Parse("2006-01-02", dateStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "date must be a valid date"})
		return
	}

	notifications, err := n.notificationService.GetNotificationsByDate(userID, date)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, notifications)
}

func (n *NotificationController) GetNotificationsByType(c *gin.Context) {
	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
		return
	}

	typeStr := c.Param("type")
	typeStr = strings.ToLower(typeStr)
	if typeStr == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "type is required"})
		return
	}

	notifications, err := n.notificationService.GetNotificationsByType(userID, typeStr)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, notifications)
}

func (n *NotificationController) SendTestNotification(c *gin.Context) {
	userID := c.Param("user_id")
	if userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "user_id required"})
		return
	}

	err := fb.FcmHandler.SendNotification(
		userID,
		"Test Title",
		"Test Description",
		false,
		model.SystemNotification,
	)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}
