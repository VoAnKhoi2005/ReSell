package service

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/google/uuid"
)

type SubscriptionService interface {
	GetAllPlans() ([]*model.SubscriptionPlan, error)
	GetPlanByID(id string) (*model.SubscriptionPlan, error)
	CreatePlan(req *transaction.CreateSubscriptionPlanRequest) (*model.SubscriptionPlan, error)
	UpdatePlan(id string, req *transaction.UpdateSubscriptionPlanRequest) (*model.SubscriptionPlan, error)
	DeletePlan(id string) error
}

type subscriptionService struct {
	repo repository.SubscriptionRepository
}

func NewSubscriptionService(repo repository.SubscriptionRepository) SubscriptionService {
	return &subscriptionService{repo: repo}
}

func (s *subscriptionService) GetAllPlans() ([]*model.SubscriptionPlan, error) {
	return s.repo.GetAll()
}

func (s *subscriptionService) GetPlanByID(id string) (*model.SubscriptionPlan, error) {
	return s.repo.GetByID(id)
}

func (s *subscriptionService) CreatePlan(req *transaction.CreateSubscriptionPlanRequest) (*model.SubscriptionPlan, error) {
	plan := &model.SubscriptionPlan{
		ID:            uuid.New().String(),
		Name:          req.Name,
		Description:   req.Description,
		Duration:      req.Duration,
		StripePriceID: req.StripePriceID,
	}
	err := s.repo.Create(plan)
	return plan, err
}

func (s *subscriptionService) UpdatePlan(id string, req *transaction.UpdateSubscriptionPlanRequest) (*model.SubscriptionPlan, error) {
	plan, err := s.repo.GetByID(id)
	if err != nil {
		return nil, err
	}

	plan.Name = req.Name
	plan.Description = req.Description
	plan.Duration = req.Duration
	plan.StripePriceID = req.StripePriceID

	err = s.repo.Update(plan)
	return plan, err
}

func (s *subscriptionService) DeletePlan(id string) error {
	plan, err := s.repo.GetByID(id)
	if err != nil {
		return err
	}
	return s.repo.Delete(plan)
}
