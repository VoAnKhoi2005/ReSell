package service

import "github.com/VoAnKhoi2005/ReSell/repository"

type OrderService interface {
}

type OderService struct {
	orderService *repository.OrderRepository
}

func NewOrderService(repo *repository.OrderRepository) OrderService {
	return &OderService{orderService: repo}
}
