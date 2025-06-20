package service

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
)

type PaymentMethodService interface {
	GetAllPaymentMethods() ([]*model.PaymentMethod, error)
	GetPaymentMethodByID(id string) (*model.PaymentMethod, error)
	CreatePaymentMethod(req *transaction.CreatePaymentMethodRequest) (*model.PaymentMethod, error)
	UpdatePaymentMethod(id string, req *transaction.UpdatePaymentMethodRequest) (*model.PaymentMethod, error)
	DeletePaymentMethod(id string) error
}

type paymentMethodService struct {
	repo repository.PaymentMethodRepository
}

func NewPaymentMethodService(repo repository.PaymentMethodRepository) PaymentMethodService {
	return &paymentMethodService{
		repo: repo,
	}
}

func (s *paymentMethodService) GetAllPaymentMethods() ([]*model.PaymentMethod, error) {
	return s.repo.GetAll()
}

func (s *paymentMethodService) GetPaymentMethodByID(id string) (*model.PaymentMethod, error) {
	return s.repo.GetById(id)
}

func (s *paymentMethodService) CreatePaymentMethod(req *transaction.CreatePaymentMethodRequest) (*model.PaymentMethod, error) {
	paymentMethod := &model.PaymentMethod{
		Name: req.Name,
	}

	err := s.repo.Create(paymentMethod)

	return paymentMethod, err
}

func (s *paymentMethodService) DeletePaymentMethod(id string) error {
	category, err := s.repo.GetById(id)
	if err != nil {
		return err
	}
	return s.repo.Delete(category)
}

func (s *paymentMethodService) UpdatePaymentMethod(id string, req *transaction.UpdatePaymentMethodRequest) (*model.PaymentMethod, error) {
	paymentMethod, err := s.repo.GetById(id)

	if err != nil {
		return nil, err
	}
	paymentMethod.Name = req.Name
	err = s.repo.Update(paymentMethod)
	return paymentMethod, err
}
