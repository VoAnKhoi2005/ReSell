package transaction

type CreateOrderRequest struct {
	PostID    string `json:"post_id" binding:"required"`
	AddressID string `json:"address_id" binding:"required"`
	Total     int    `json:"total" binding:"required"`
}

type CreateReviewRequest struct {
	OrderID string `json:"order_id" binding:"required"`
	Rating  int    `json:"rating" binding:"required"`
	Comment string `json:"comment"`
}
