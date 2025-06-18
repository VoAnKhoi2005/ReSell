package data

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/google/uuid"
)

func seedPaymentMethod() []string {
	var paymentMethods []model.PaymentMethod
	var paymentMethodIDs []string

	paymentMethods = append(paymentMethods, model.PaymentMethod{
		ID:   uuid.New().String(),
		Name: "Thanh toan truc tiep",
	})

	paymentMethods = append(paymentMethods, model.PaymentMethod{
		ID: uuid.New().String(),
		Name: "Chuyen khoan" +
			"",
	})

	for _, paymentMethod := range paymentMethods {
		paymentMethodIDs = append(paymentMethodIDs, paymentMethod.ID)
	}

	config.DB.Create(&paymentMethods)
	return paymentMethodIDs
}
