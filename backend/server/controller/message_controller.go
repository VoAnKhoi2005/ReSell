package controller

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/VoAnKhoi2005/ReSell/util"
	"github.com/gin-gonic/gin"
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

type CreateConversationRequest struct {
	BuyerID  *string `json:"buyer_id" binding:"required"`
	SellerID *string `json:"seller_id" binding:"required"`
	PostID   *string `json:"post_id" binding:"required"`
}

func (mc *MessageController) CreateConversation(c *gin.Context) {
	var request CreateConversationRequest
	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if !util.IsUserOwner(c, request.BuyerID) {
		return
	}

	//
	//Thiếu kiểm tra post có tồn tại không
	//

	conversation := model.Conversation{
		BuyerId:   request.BuyerID,
		SellerId:  request.SellerID,
		PostId:    request.PostID,
		CreatedAt: time.Now(),
	}

	err = mc.messageService.CreateConversation(&conversation)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"conversation": conversation})
}

type CreateMessageRequest struct {
	Content        string  `json:"content" binding:"required"`
	ConversationId *string `json:"conversationId" binding:"required"`
	SenderId       *string `json:"senderId" binding:"required"`
}

func (mc *MessageController) CreateMessage(c *gin.Context) {
	var request CreateMessageRequest
	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if !util.IsUserOwner(c, request.SenderId) {
		return
	}

	conversation, err := mc.messageService.GetConversationByID(*request.ConversationId)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	if conversation == nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "conversation not found"})
		return
	}

	message := model.Message{
		Content:        request.Content,
		ConversationId: request.ConversationId,
		SenderId:       request.SenderId,
		CreatedAt:      time.Now(),
	}

	err = mc.messageService.CreateMessage(&message)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": message})
}

func (mc *MessageController) GetMessagesInRange(c *gin.Context) {
	conversationId := c.Param("id")
	conversation, err := mc.messageService.GetConversationByID(conversationId)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	if conversation == nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "conversation not found"})
		return
	}

	if !util.IsUserOwner(c, conversation.BuyerId) && !util.IsUserOwner(c, conversation.SellerId) {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "not authorized"})
		return
	}

	startStr := c.Query("start")
	endStr := c.Query("end")

	start, err1 := strconv.Atoi(startStr)
	end, err2 := strconv.Atoi(endStr)

	if err1 != nil || err2 != nil || start < 0 || end <= start {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid range"})
		return
	}

	messages, err := mc.messageService.GetMessagesInRange(conversationId, uint(start), uint(end))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"messages": messages})
}

func (mc *MessageController) GetLatestMessages(c *gin.Context) {
	conversationID := c.Param("id")
	amountStr := c.Param("amount")
	amount, err := strconv.Atoi(amountStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if amount < 1 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "`amount` must be greater than zero"})
		return
	}

	messages, err := mc.messageService.GetLatestMessages(conversationID, uint(amount))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"messages": messages})
}
