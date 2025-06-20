package transaction

type CreateTransactionRequest struct {
	OrderID string `json:"order_id" binding:"required,uuid"`
}
