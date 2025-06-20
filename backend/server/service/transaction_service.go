package service

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	request "github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/google/uuid"
	"github.com/stripe/stripe-go/v78"
	"github.com/stripe/stripe-go/v78/paymentintent"
)

type TransactionService interface {
	CreateTransaction(req request.CreateTransactionRequest) (string, string, error)
	GetTransactions(page, limit int) ([]*model.Transaction, int64, error)
	MarkTransactionSuccess(paymentIntentID string) error
	MarkTransactionFailed(paymentIntentID string, reason string) error
}

type transactionService struct {
	txRepo    repository.TransactionRepository
	orderRepo repository.OrderRepository
}

func NewTransactionService(tx repository.TransactionRepository, ord repository.OrderRepository) TransactionService {
	return &transactionService{
		txRepo:    tx,
		orderRepo: ord,
	}
}

func (s *transactionService) GetTransactions(page, limit int) ([]*model.Transaction, int64, error) {
	return s.txRepo.GetPaginated(page, limit)
}

func (s *transactionService) CreateTransaction(req request.CreateTransactionRequest) (string, string, error) {
	// 1. Kiểm tra order tồn tại
	order, err := s.orderRepo.GetByID(req.OrderID)
	if err != nil {
		return "", "", fmt.Errorf("order not found: %w", err)
	}

	// 2. Tạo PaymentIntent với Stripe
	pi, err := paymentintent.New(&stripe.PaymentIntentParams{
		Amount:   stripe.Int64(int64(order.Total)), // đơn vị là cents
		Currency: stripe.String("vnd"),
	})
	if err != nil {
		return "", "", fmt.Errorf("stripe error: %w", err)
	}

	// 3. Tạo Transaction trong DB
	tx := model.Transaction{
		ID:                    uuid.New().String(),
		OrderID:               req.OrderID,
		UserID:                *order.UserId,
		Amount:                order.Total,
		StripePaymentIntentID: pi.ID,
		Status:                model.TransactionStatusPending,
		PaymentMethodID:       *order.PaymentMethodId,
	}
	err = s.txRepo.Create(&tx)
	if err != nil {
		return "", "", err
	}

	return pi.ClientSecret, tx.ID, nil
}

func (s *transactionService) MarkTransactionSuccess(paymentIntentID string) error {
	return s.txRepo.UpdateStatusByIntentID(paymentIntentID, model.TransactionStatusCompleted)
}

func (s *transactionService) MarkTransactionFailed(paymentIntentID string, reason string) error {
	return s.txRepo.UpdateStatusAndErrorByIntentID(paymentIntentID, "failed", reason)
}
