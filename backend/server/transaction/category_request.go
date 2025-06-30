package transaction

type CreateCategoryRequest struct {
	ParentCategoryId string `json:"parent_category_id"`
	Name             string `form:"name" json:"name" binding:"required"`
	ImageURL         string `json:"image_url"`
}

type UpdateCategoryRequest struct {
	Name     string `form:"name" json:"name" binding:"required"`
	ImageURL string `json:"image_url"`
}
