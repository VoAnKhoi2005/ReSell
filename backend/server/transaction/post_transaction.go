package transaction

type CreatePostRequest struct {
	CategoryID  string `json:"category_id" binding:"required"`
	WardID      string `json:"ward_id" binding:"required"`
	Title       string `json:"title" binding:"required"`
	Description string `json:"description" binding:"required"`
	Price       uint   `json:"price" binding:"required"`
}

type UpdatePostRequest struct {
	CategoryID  *string `json:"category_id"`
	WardID      *string `json:"ward_id"`
	Title       *string `json:"title"`
	Description *string `json:"description"`
	Price       *uint   `json:"price"`
}
