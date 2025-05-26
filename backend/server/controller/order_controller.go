package controller

import "github.com/VoAnKhoi2005/ReSell/service"

type OrderController struct {
	orderService *service.OrderService
}

func NewOrderController(orderService *service.OrderService) *OrderController {
	return &OrderController{orderService: orderService}
}
