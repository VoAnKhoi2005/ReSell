package transaction

type CreatePaymentMethodRequest struct {
	Name string `json:"name"`
}

type UpdatePaymentMethodRequest struct {
	Name string `json:"name"`
}
