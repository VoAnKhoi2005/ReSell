package transaction

import "github.com/VoAnKhoi2005/ReSell/backend/server/model"

type CreateConversationRequest struct {
	BuyerID  string `json:"buyer_id" binding:"required"`
	SellerID string `json:"seller_id" binding:"required"`
	PostID   string `json:"post_id" binding:"required"`
}

type GetConversationResponse struct {
	Conversation *model.Conversation `json:"conversation,omitempty"`
	IsExist      bool                `json:"is_exist"`
}

type CreateOfferRequest struct {
	ConversationID string `json:"conversation_id" binding:"required"`
	Amount         int    `json:"amount" binding:"required"`
}

type CreateMessageRequest struct {
	Content        string `json:"content" binding:"required"`
	ConversationId string `json:"conversationId" binding:"required"`
	SenderId       string `json:"senderId" binding:"required"`
}
