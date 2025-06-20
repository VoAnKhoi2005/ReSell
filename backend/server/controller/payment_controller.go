package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	request "github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/gin-gonic/gin"
	"net/http"
)

type PaymentMethodController struct {
	service service.PaymentMethodService
}

func NewPaymentMethodController(service service.PaymentMethodService) *PaymentMethodController {
	return &PaymentMethodController{
		service: service,
	}
}

func (h *PaymentMethodController) GetAllPaymentMethods(c *gin.Context) {
	paymentMethods, err := h.service.GetAllPaymentMethods()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, paymentMethods)

}

func (h *PaymentMethodController) GetPaymentMethodByID(c *gin.Context) {
	id := c.Param("id")
	paymentMethod, err := h.service.GetPaymentMethodByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, paymentMethod)
}

func (h *PaymentMethodController) CreatePaymentMethod(c *gin.Context) {
	var req request.CreatePaymentMethodRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	paymentMethod, err := h.service.CreatePaymentMethod(&req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": paymentMethod.ID, "message": "PaymentMethod created"})
}

func (h *PaymentMethodController) UpdatePaymentMethod(c *gin.Context) {
	var req request.UpdatePaymentMethodRequest

	id := c.Param("id")

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	paymentMethod, err := h.service.UpdatePaymentMethod(id, &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": paymentMethod.ID, "message": "PaymentMethod updated"})

}

func (h *PaymentMethodController) DeletePaymentMethod(c *gin.Context) {
	id := c.Param("id")
	err := h.service.DeletePaymentMethod(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "PaymentMethod deleted"})
}
