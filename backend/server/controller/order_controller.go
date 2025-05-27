package controller

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/gin-gonic/gin"
	"net/http"
	"time"
)

type OrderController struct {
	orderService service.OrderService
}

func NewOrderController(orderService service.OrderService) *OrderController {
	return &OrderController{orderService: orderService}
}

func (oc *OrderController) CreateOrder(c *gin.Context) {
	var request transaction.CreateOrderRequest
	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	order := model.ShopOrder{
		UserId:    &request.UserID,
		PostId:    &request.PostID,
		Status:    request.Status,
		AddressId: &request.AddressID,
		Total:     request.Total,
		CreatedAt: time.Now(),
	}

	err = oc.orderService.CreateOrder(&order)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"success": true})
}

func (oc *OrderController) DeleteOrder(c *gin.Context) {
	orderID := c.Param("order_id")
	if orderID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "order id is required"})
		return
	}

	err := oc.orderService.DeleteOrder(orderID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
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

	c.JSON(http.StatusOK, gin.H{"order": order})
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

	c.JSON(http.StatusOK, gin.H{"order": order})
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

	c.JSON(http.StatusOK, gin.H{"order": order})
}

func (oc *OrderController) UpdateStatus(c *gin.Context) {
	orderID := c.Param("order_id")
	if orderID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "order id is required"})
		return
	}

	newStatus := c.PostForm("new_status")
	if newStatus == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "new status is required"})
		return
	}

	err := oc.orderService.UpdateStatus(orderID, newStatus)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (oc *OrderController) CreateReview(c *gin.Context) {
	var request transaction.CreateReviewRequest
	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	review := model.UserReview{
		UserId:  &request.UserID,
		OrderId: &request.OrderID,
		Rating:  request.Rating,
		Comment: request.Comment,
	}

	err = oc.orderService.CreateReview(&review)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (oc *OrderController) DeleteReview(c *gin.Context) {
	reviewID := c.Param("review_id")
	if reviewID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "review id is required"})
		return
	}

	err := oc.orderService.DeleteReview(reviewID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}
