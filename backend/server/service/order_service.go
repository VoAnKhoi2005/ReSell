package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
)

type OrderService interface {
	CreateOrder(order *model.ShopOrder) error
	DeleteOrder(orderID string, userID string) error

	GetByID(orderID string) (*model.ShopOrder, error)
	GetByPostID(postID string) (*model.ShopOrder, error)
	GetByBuyerID(buyerID string) ([]*model.ShopOrder, error)
	GetBySellerID(sellerID string) ([]*model.ShopOrder, error)

	UpdateStatus(orderID string, userID string, status model.OrderStatus) error
}

type OderService struct {
	orderRepository   repository.OrderRepository
	postRepository    repository.PostRepository
	addressRepository repository.AddressRepository
}

func NewOrderService(orderRepo repository.OrderRepository, postRepo repository.PostRepository, addressRepo repository.AddressRepository) OrderService {
	return &OderService{orderRepository: orderRepo, postRepository: postRepo, addressRepository: addressRepo}
}

func (o *OderService) CreateOrder(order *model.ShopOrder) error {
	post, err := o.postRepository.GetByID(*order.PostId)
	if err != nil {
		return err
	}

	if *post.UserID == *order.UserId {
		return errors.New("user can not create order for their own post")
	}

	address, err := o.addressRepository.GetByID(*order.AddressId)
	if err != nil {
		return err
	}

	if *address.UserID != *order.UserId {
		return errors.New("address is not owned by this user")
	}

	return o.orderRepository.Create(order)
}

func (o *OderService) DeleteOrder(orderID string, userID string) error {
	order, err := o.orderRepository.GetByID(orderID)
	if err != nil {
		return err
	}

	if *order.UserId != userID {
		return errors.New("you are not the owner of the order")
	}

	if order.Status != "Processing" {
		return errors.New("the order can not be deleted now")
	}

	return o.orderRepository.Delete(order)
}

func (o *OderService) GetByID(orderID string) (*model.ShopOrder, error) {
	return o.orderRepository.GetByID(orderID)
}

func (o *OderService) GetByPostID(postID string) (*model.ShopOrder, error) {
	return o.orderRepository.GetByPostID(postID)
}

func (o *OderService) GetByBuyerID(buyerID string) ([]*model.ShopOrder, error) {
	return o.orderRepository.GetByBuyerID(buyerID)
}

func (o *OderService) GetBySellerID(sellerID string) ([]*model.ShopOrder, error) {
	return o.orderRepository.GetBySellerID(sellerID)
}

func (o *OderService) UpdateStatus(orderID string, userID string, status model.OrderStatus) error {
	order, err := o.orderRepository.GetByID(orderID)
	if err != nil {
		return err
	}

	post, err := o.postRepository.GetByID(*order.PostId)
	if err != nil {
		return err
	}

	if *post.UserID != userID {
		return errors.New("unauthorized to change status of the order")
	}

	order.Status = status
	return o.orderRepository.Update(order)
}
