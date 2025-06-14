package transaction

type CreateConversationRequest struct {
	BuyerID  string `json:"buyer_id" binding:"required"`
	SellerID string `json:"seller_id" binding:"required"`
	PostID   string `json:"post_id" binding:"required"`
}

type CreateMessageRequest struct {
	Content        string `json:"content" binding:"required"`
	ConversationId string `json:"conversationId" binding:"required"`
	SenderId       string `json:"senderId" binding:"required"`
}
