package transaction

type CreateSubscriptionRequest struct {
	PlanID string `json:"plan_id" binding:"required"`
}
