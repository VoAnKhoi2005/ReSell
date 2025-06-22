package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/stripe/stripe-go/v78"
	acct "github.com/stripe/stripe-go/v78/account"
	acctlink "github.com/stripe/stripe-go/v78/accountlink"
	"github.com/stripe/stripe-go/v78/transfer"
)

// StripeService định nghĩa các hàm cần cho controller gọi
type StripeService interface {
	CreateExpressAccount(userID string) (string, error)
	GetOnboardingLink(userID string) (string, error)
	TransferToSeller(userID string, amount int64) error
	MarkTransactionSuccess(paymentIntentID string) error
	MarkTransactionFailed(paymentIntentID string, reason string) error
	MarkStripeVerified(accountID string) error
}

// stripeService là struct chính, nhận UserRepo qua constructor
type stripeService struct {
	userRepo repository.UserRepository // interface đã được định nghĩa từ trước
	txRepo   repository.TransactionRepository
}

// Constructor
func NewStripeService(userRepo repository.UserRepository, txRepo repository.TransactionRepository) StripeService {
	return &stripeService{
		userRepo: userRepo,
		txRepo:   txRepo,
	}
}

// Tạo Stripe Express Account
func (s *stripeService) CreateExpressAccount(userID string) (string, error) {
	// 1. Tạo Stripe account
	params := &stripe.AccountParams{
		Type: stripe.String(string(stripe.AccountTypeExpress)),
	}
	account, err := acct.New(params)
	if err != nil {
		return "", err
	}

	// 2. Lấy user từ DB
	user, err := s.userRepo.GetByID(userID)
	if err != nil {
		return "", err
	}

	// 3. Gán thông tin Stripe cho user
	user.StripeAccountID = &account.ID
	user.IsSelling = true
	user.IsStripeVerified = false // mặc định chưa xác minh

	// 4. Lưu lại
	if err := s.userRepo.Update(user); err != nil {
		return "", err
	}

	return account.ID, nil
}

// Tạo onboarding link để user hoàn tất setup
func (s *stripeService) GetOnboardingLink(userID string) (string, error) {
	accountID, err := s.userRepo.GetStripeAccountID(userID)
	if err != nil {
		return "", err
	}
	if accountID == "" {
		return "", errors.New("user has no Stripe account ID")
	}

	params := &stripe.AccountLinkParams{
		Account:    stripe.String(accountID),
		RefreshURL: stripe.String("http://localhost:8080/api/stripe/onboarding-failed"),
		ReturnURL:  stripe.String("http://localhost:8080/api/stripe/onboarding-success"),
		Type:       stripe.String("account_onboarding"),
	}
	link, err := acctlink.New(params)
	if err != nil {
		return "", err
	}

	return link.URL, nil
}

// Chuyển tiền cho seller
func (s *stripeService) TransferToSeller(userID string, amount int64) error {
	accountID, err := s.userRepo.GetStripeAccountID(userID)
	if err != nil {
		return err
	}
	if accountID == "" {
		return errors.New("user has no Stripe account ID")
	}

	params := &stripe.TransferParams{
		Amount:      stripe.Int64(amount),
		Currency:    stripe.String("vnd"),
		Destination: stripe.String(accountID),
	}
	_, err = transfer.New(params)
	return err
}

func (s *stripeService) MarkTransactionSuccess(paymentIntentID string) error {
	return s.txRepo.UpdateStatusByIntentID(paymentIntentID, model.TransactionStatusCompleted)
}

func (s *stripeService) MarkTransactionFailed(paymentIntentID string, reason string) error {
	return s.txRepo.UpdateStatusAndErrorByIntentID(paymentIntentID, "failed", reason)
}

func (s *stripeService) MarkStripeVerified(accountID string) error {
	user, err := s.userRepo.GetByStripeAccountID(accountID)
	if err != nil {
		return err
	}

	user.IsStripeVerified = true
	return s.userRepo.Update(user)
}
