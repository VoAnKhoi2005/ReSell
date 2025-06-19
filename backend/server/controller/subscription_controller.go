package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	request "github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/gin-gonic/gin"
	"net/http"
)

type SubscriptionController struct {
	service service.SubscriptionService
}

func NewSubscriptionController(service service.SubscriptionService) *SubscriptionController {
	return &SubscriptionController{service: service}
}

func (h *SubscriptionController) GetAllPlans(c *gin.Context) {
	plans, err := h.service.GetAllPlans()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, plans)
}

func (h *SubscriptionController) GetPlanByID(c *gin.Context) {
	id := c.Param("id")
	plan, err := h.service.GetPlanByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, plan)
}

func (h *SubscriptionController) CreatePlan(c *gin.Context) {
	var req request.CreateSubscriptionPlanRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	plan, err := h.service.CreatePlan(&req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": plan.ID, "message": "Subscription plan created"})
}

func (h *SubscriptionController) UpdatePlan(c *gin.Context) {
	id := c.Param("id")
	var req request.UpdateSubscriptionPlanRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	plan, err := h.service.UpdatePlan(id, &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"id": plan.ID, "message": "Subscription plan updated"})
}

func (h *SubscriptionController) DeletePlan(c *gin.Context) {
	id := c.Param("id")
	err := h.service.DeletePlan(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Subscription plan deleted"})
}
