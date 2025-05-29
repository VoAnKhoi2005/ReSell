package transaction

type CreateCartRequest struct {
	PostID string `json:"post_id" binding:"required"`
}
