package service

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
)

type OrderService interface {
	CreateOrder(order *model.ShopOrder) error
	DeleteOrder(orderID string) error

	GetByID(orderID string) (*model.ShopOrder, error)
	GetByPostID(PostID string) (*model.ShopOrder, error)
	GetByBuyerID(BuyerID string) (*model.ShopOrder, error)
	GetBySellerID(SellerID string) (*model.ShopOrder, error)

	UpdateStatus(orderID string, status string) error

	CreateReview(review *model.UserReview) error
	DeleteReview(reviewID string) error
}

type OderService struct {
	orderRepository repository.OrderRepository
	postRepository  repository.PostRepository
}

func NewOrderService(orderRepo repository.OrderRepository, postRepo repository.PostRepository) OrderService {
	return &OderService{orderRepository: orderRepo, postRepository: postRepo}
}

func (o *OderService) CreateOrder(order *model.ShopOrder) error {
	return o.orderRepository.Create(order)
}

func (o *OderService) DeleteOrder(orderID string) error {
	order, err := o.orderRepository.GetByID(orderID)
	if err != nil {
		return err
	}

	return o.orderRepository.Delete(order)
}

func (o *OderService) GetByID(orderID string) (*model.ShopOrder, error) {
	return o.orderRepository.GetByID(orderID)
}

func (o *OderService) GetByPostID(PostID string) (*model.ShopOrder, error) {
	return o.orderRepository.GetByPostID(PostID)
}

func (o *OderService) GetByBuyerID(BuyerID string) (*model.ShopOrder, error) {
	return o.orderRepository.GetByBuyerID(BuyerID)
}

func (o *OderService) GetBySellerID(SellerID string) (*model.ShopOrder, error) {
	//TODO implement me
	panic("implement me")
}

func (o *OderService) UpdateStatus(orderID string, status string) error {
	order, err := o.orderRepository.GetByID(orderID)
	if err != nil {
		return err
	}

	order.Status = status
	return o.orderRepository.Update(order)
}

func (o *OderService) CreateReview(review *model.UserReview) error {
	return o.orderRepository.CreateReview(review)
}

func (o *OderService) DeleteReview(reviewID string) error {
	review, err := o.orderRepository.GetReviewByID(reviewID)
	if err != nil {
		return err
	}

	return o.orderRepository.DeleteReview(review)
}
