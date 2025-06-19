package service

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/stripe/stripe-go/v78"
)

type SubscriptionService inteface {
	CreateCheckoutSession(userID, planID string) (string, error)
}

type subscriptionService struct {
	repo repository.SubscriptionRepository
}

func NewSubscriptionService(repo repository.SubscriptionRepository) SubscriptionService {
	return &subscriptionService{repo: repo}
}

func (s *subscriptionService) CreateCheckoutSession(userID string, planID string) (string, error) {
	plan, err := s.repo.GetPlanByID(planID)
	if err != nil {
		return "", err
	}

	user, err := s.repo.GetUserByID(userID)
	if err != nil {
		return "", err
	}

	params := &stripe.CheckoutSessionParams{
		SuccessURL: stripe.String("https://your-frontend.com/success"),
		CancelURL:  stripe.String("https://your-frontend.com/cancel"),
		Mode:       stripe.String(string(stripe.CheckoutSessionModeSubscription)),
		LineItems: []*stripe.CheckoutSessionLineItemParams{
			{
				Price:    stripe.String(plan.StripePriceID),
				Quantity: stripe.Int64(1),
			},
		},
		CustomerEmail: stripe.String(user.Email),
	}

	session, err := checkoutsession.New(params)
	if err != nil {
		return "", fmt.Errorf("stripe error: %v", err)
	}

	return session.URL, nil
}
