package transaction

type CreateOrderRequest struct {
	UserID    string `json:"user_id" binding:"required"`
	PostID    string `json:"post_id" binding:"required"`
	Status    string `json:"status" binding:"required"`
	AddressID string `json:"address_id" binding:"required"`
	Total     int    `json:"total" binding:"required"`
}
