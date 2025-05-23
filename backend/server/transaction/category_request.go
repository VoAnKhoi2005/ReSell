package transaction

type CreateCategoryRequest struct {
	ParentCategoryId string `json:"parent_category_id"`
	Name             string `json:"name" binding:"required"`
}

type UpdateCategoryRequest struct {
	Name string `json:"name" binding:"required"`
}
