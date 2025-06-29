package controller

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
	"net/http"
	"strconv"
	"time"
)

type MessageController struct {
	messageService service.MessageService
}

func NewMessageController(messageService service.MessageService) *MessageController {
	return &MessageController{
		messageService: messageService,
	}
}

func (mc *MessageController) CreateConversation(c *gin.Context) {
	var request transaction.CreateConversationRequest
	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if !util.IsUserOwner(c, request.BuyerID) {
		return
	}

	conversation := model.Conversation{
		BuyerId:   &request.BuyerID,
		SellerId:  &request.SellerID,
		PostId:    &request.PostID,
		CreatedAt: time.Now().UTC(),
	}

	createdConversation, err := mc.messageService.CreateConversation(&conversation)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, createdConversation)
}

func (mc *MessageController) GetConversationByID(c *gin.Context) {
	conversationID := c.Param("id")
	if conversationID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "conversation id is required"})
		return
	}

	conversation, err := mc.messageService.GetConversationByID(conversationID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, conversation)
}

func (mc *MessageController) GetConversationByPostID(c *gin.Context) {
	postID := c.Param("post_id")
	if postID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "post id is required"})
		return
	}

	conversations, err := mc.messageService.GetConversationsByPostID(postID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, conversations)
}

func (mc *MessageController) GetConversationsStatByUserID(c *gin.Context) {
	userID, err := util.GetUserID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	conversations, err := mc.messageService.GetConversationStatDTOByUserID(userID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, conversations)
}

func (mc *MessageController) GetConversationsByUserID(c *gin.Context) {
	userID, err := util.GetUserID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	conversations, err := mc.messageService.GetConversationsByUserID(userID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, conversations)
}

func (mc *MessageController) GetConversationByUserAndPostID(c *gin.Context) {
	postID := c.Param("post_id")
	if postID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "post id is required"})
		return
	}

	userID, err := util.GetUserID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	conversation, err := mc.messageService.GetConversationByUserAndPostID(userID, postID)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusOK, transaction.GetConversationResponse{Conversation: nil, IsExist: false})
			return
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
	}

	c.JSON(http.StatusOK, transaction.GetConversationResponse{Conversation: conversation, IsExist: true})
}

func (mc *MessageController) DeleteConversation(c *gin.Context) {
	conversationID := c.Param("id")
	if conversationID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "conversation id is required"})
		return
	}

	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
		return
	}

	err = mc.messageService.DeleteConversation(userID, conversationID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, true)
}

func (mc *MessageController) GetLatestMessages(c *gin.Context) {
	conversationID := c.Param("id")
	amountStr := c.Param("amount")
	amount, err := strconv.Atoi(amountStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	messages, err := mc.messageService.GetLatestMessages(conversationID, uint(amount))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, messages)
}

func (mc *MessageController) GetLatestMessagesByBatch(c *gin.Context) {
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

	conversationID := c.Param("id")
	if conversationID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "conversation id is required"})
		return
	}

	messages, totalBatchCount, err := mc.messageService.GetLatestMessagesByBatch(conversationID, batchSize, page)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"messages": messages, "total_batch_count": totalBatchCount})
}

func (mc *MessageController) UploadImage(c *gin.Context) {
	file, fileHeader, err := c.Request.FormFile("image")
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	defer file.Close()

	imageURL, err := util.UploadToCloudinary(file, fileHeader)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return

	}
	c.JSON(http.StatusOK, imageURL)
}
