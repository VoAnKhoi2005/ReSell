package service

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/zalo"
	"log"
	"time"
)

type OrderService interface {
	CreateOrder(order *model.ShopOrder) error
	DeleteOrder(orderID string, userID string) error

	GetByID(orderID string) (*model.ShopOrder, error)
	GetByPostID(postID string) (*model.ShopOrder, error)
	GetByBuyerID(buyerID string) ([]*model.ShopOrder, error)
	GetBySellerID(sellerID string) ([]*model.ShopOrder, error)

	UpdateStatus(orderID string, userID string, status model.OrderStatus) error

	GetBuyerID(orderID string) (string, error)

	CreateZaloPayOrder(orderID string, userID string) (string, error)
	HandleZaloPayCallback(callbackData map[string]interface{}) (*model.ShopOrder, error)
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

func (o *OderService) UpdateStatus(orderID string, sellerID string, status model.OrderStatus) error {
	order, err := o.orderRepository.GetByID(orderID)
	if err != nil {
		return err
	}

	post, err := o.postRepository.GetByID(*order.PostId)
	if err != nil {
		return err
	}

	if *post.UserID != sellerID {
		return errors.New("unauthorized to change status of the order")
	}

	if order.Status == model.OrderStatusCancelled {
		return errors.New("the order has been cancelled and cannot be updated")
	}

	order.Status = status
	err = o.orderRepository.Update(order)
	if err != nil {
		return err
	}

	//Handle notification
	buyerID, err := o.GetBuyerID(orderID)
	if err != nil {
		return err
	}

	newStatus := order.Status

	var title, description string
	if newStatus == model.OrderStatusShipping {
		title, description = model.DefaultNotificationContent(model.OrderNotification)
		err = fb.FcmHandler.SendNotification(buyerID, title, description, false, model.OrderNotification)
		log.Printf("error sending order notification %v", err)
	}

	if newStatus == model.OrderStatusProcessing {
		title = "Order Processing"
		description = "Your order has been create successfully and is being processed"
		err = fb.FcmHandler.SendNotification(buyerID, title, description, false, model.OrderNotification)
		log.Printf("error sending order notification %v", err)
	}

	if newStatus == model.OrderStatusCancelled {
		title = "Order Cancelled"
		description = "Your order has been cancelled"
		err = fb.FcmHandler.SendNotification(buyerID, title, description, false, model.OrderNotification)
		log.Printf("error sending order notification %v", err)

		err = repository.GlobalRepo.DecreaseReputation(sellerID, 10)
		if err != nil {
			return err
		}
	}

	if newStatus == model.OrderStatusCompleted {
		title = "Order Completed"
		description = "Your order has been delivered successfully"
		err = fb.FcmHandler.SendNotification(sellerID, title, description, false, model.OrderNotification)
		err = fb.FcmHandler.SendNotification(buyerID, title, description, false, model.OrderNotification)
		log.Printf("error sending order notification %v", err)

		err = repository.GlobalRepo.IncreaseReputation(sellerID, 20)
		if err != nil {
			return err
		}
	}

	return nil
}

func (o *OderService) GetBuyerID(orderID string) (string, error) {
	order, err := o.orderRepository.GetByID(orderID)
	if err != nil {
		return "", err
	}

	return *order.UserId, nil
}

func (o *OderService) CreateZaloPayOrder(orderID string, userID string) (string, error) {
	order, err := o.orderRepository.GetByID(orderID)
	if err != nil {
		return "", err
	}

	if *order.UserId != userID {
		return "", errors.New("unauthorized to pay for this order")
	}

	if order.PaymentStatus == model.PaymentPaid {
		return "", errors.New("this order has already been paid")
	}

	// Tạo mã app_trans_id theo chuẩn ZaloPay: yymmdd_xxxx
	appTransID := zalo.GenerateAppTransID()
	paymentURL, zaloTransID, err := zalo.CreateZaloPayOrder(appTransID, *order.UserId, order.Total)
	if err != nil {
		return "", err
	}

	order.ZaloAppTransID = &appTransID
	order.ZaloTransID = &zaloTransID
	order.PaymentStatus = model.PaymentPending

	err = o.orderRepository.Update(order)
	if err != nil {
		return "", err
	}

	return paymentURL, nil
}

func (o *OderService) HandleZaloPayCallback(callback map[string]interface{}) (*model.ShopOrder, error) {
	// Lấy raw data và mac từ callback
	rawData, ok := callback["data"].(string)
	if !ok {
		return nil, errors.New("missing data field")
	}
	mac, ok := callback["mac"].(string)
	if !ok {
		return nil, errors.New("missing mac field")
	}

	// Verify MAC với key2
	calculatedMac := generateMac(rawData, zalo.Key2)
	if mac != calculatedMac {
		return nil, errors.New("invalid callback MAC")
	}

	// Parse rawData thành JSON map
	var data map[string]interface{}
	if err := json.Unmarshal([]byte(rawData), &data); err != nil {
		return nil, errors.New("invalid JSON in data field")
	}

	appTransID := data["app_trans_id"].(string)
	zpTransID := fmt.Sprintf("%.0f", data["zp_trans_id"].(float64))
	serverTime := int64(data["server_time"].(float64))

	order, err := o.orderRepository.GetByAppTransID(appTransID)
	if err != nil {
		return nil, err
	}

	// Cập nhật đơn hàng
	order.PaymentStatus = model.PaymentPaid
	order.ZaloTransID = &zpTransID
	paidAt := time.UnixMilli(serverTime)
	order.PaidAt = &paidAt
	order.ZaloCallbackData = &rawData
	err = o.orderRepository.Update(order)
	return order, err
}

func generateMac(data, key string) string {
	h := hmac.New(sha256.New, []byte(key))
	h.Write([]byte(data))
	return hex.EncodeToString(h.Sum(nil))
}

func ptr[T any](v T) *T {
	return &v
}
