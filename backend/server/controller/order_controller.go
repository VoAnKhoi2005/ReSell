package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"net/http"
	"time"
)

type OrderController struct {
	orderService service.OrderService
	postService  service.PostService
}

func NewOrderController(orderService service.OrderService, postService service.PostService) *OrderController {
	return &OrderController{orderService: orderService,
		postService: postService,
	}
}

func (oc *OrderController) CreateOrder(c *gin.Context) {
	var request transaction.CreateOrderRequest
	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "unauthorized"})
		return
	}

	order := model.ShopOrder{
		UserId:    &userID,
		PostId:    &request.PostID,
		Status:    model.OrderStatusPending,
		AddressId: &request.AddressID,
		Total:     request.Total,
		CreatedAt: time.Now().UTC(),
	}

	err = oc.orderService.CreateOrder(&order)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	_, err = oc.postService.MarkPostAsSold(request.PostID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, true)
}

func (oc *OrderController) DeleteOrder(c *gin.Context) {
	orderID := c.Param("order_id")
	if orderID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "order id is required"})
		return
	}

	userID, err := util.GetUserID(c)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
		return
	}

	err = oc.orderService.DeleteOrder(orderID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	order, err := oc.orderService.GetByID(orderID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	_, err = oc.postService.RevertSoldStatus(*order.PostId)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, true)
}

func (oc *OrderController) GetOrderByID(c *gin.Context) {
	orderID := c.Param("order_id")
	if orderID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "order id is required"})
		return
	}

	order, err := oc.orderService.GetByID(orderID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, order)
}

func (oc *OrderController) GetByPostID(c *gin.Context) {
	postID := c.Param("post_id")
	if postID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "post id is required"})
		return
	}

	order, err := oc.orderService.GetByPostID(postID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, order)
}

func (oc *OrderController) GetByBuyerID(c *gin.Context) {
	buyerID := c.Param("buyer_id")
	if buyerID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "buyer id is required"})
		return
	}

	order, err := oc.orderService.GetByBuyerID(buyerID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, order)
}

func (oc *OrderController) GetBySellerID(c *gin.Context) {
	sellerID := c.Param("seller_id")
	if sellerID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "seller id is required"})
		return
	}

	orders, err := oc.orderService.GetBySellerID(sellerID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, orders)
}

func (oc *OrderController) UpdateStatus(c *gin.Context) {
	orderID := c.Param("order_id")
	if orderID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "order id is required"})
		return
	}

	statusStr := c.Param("new_status")
	if statusStr == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "status is required"})
		return
	}

	newStatus, err := model.ParseOrderStatus(statusStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	sellerID, err := util.GetUserID(c)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
		return
	}

	err = oc.orderService.UpdateStatus(orderID, sellerID, newStatus)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, true)
}

func (oc *OrderController) CreateZaloPayPayment(c *gin.Context) {
	orderID := c.Param("id")
	if orderID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "order id is required"})
		return
	}

	userID, err := util.GetUserID(c)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
		return
	}

	// Gọi service tạo đơn thanh toán
	paymentURL, err := oc.orderService.CreateZaloPayOrder(orderID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"success":     true,
		"payment_url": paymentURL,
	})
}

func (oc *OrderController) HandleZaloPayCallback(c *gin.Context) {
	var callbackData map[string]interface{}
	if err := c.ShouldBindJSON(&callbackData); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"return_code": -1, "return_message": "invalid request"})
		return
	}

	order, err := oc.orderService.HandleZaloPayCallback(callbackData)
	if err != nil {
		c.JSON(http.StatusOK, gin.H{"return_code": -1, "return_message": err.Error()})
		return
	}

	title := "Payment Success"
	description := "Your order has been successfully processed"
	err = fb.FcmHandler.SendNotification(*order.UserId, title, description, false, model.OrderNotification)

	c.JSON(http.StatusOK, gin.H{"return_code": 1, "return_message": "success"})
}
