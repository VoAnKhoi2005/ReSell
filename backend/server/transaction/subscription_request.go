package transaction

type CreateSubscriptionPlanRequest struct {
	Name          string `json:"name" binding:"required"`
	Description   string `json:"description" binding:"required"`
	Duration      int    `json:"duration" binding:"required"` // đơn vị là ngày hoặc tháng tùy context
	StripePriceID string `json:"stripe_price_id" binding:"required"`
}

type UpdateSubscriptionPlanRequest struct {
	Name          string `json:"name" binding:"required"`
	Description   string `json:"description" binding:"required"`
	Duration      int    `json:"duration" binding:"required"`
	StripePriceID string `json:"stripe_price_id" binding:"required"`
}
