package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"net/http"
	"strconv"
)

type ParticipantController struct {
	service service.ParticipantService
}

func NewParticipantController(service service.ParticipantService) *ParticipantController {
	return &ParticipantController{
		service: service,
	}
}

func (h *ParticipantController) JoinCommunity(c *gin.Context) {
	communityID := c.Param("id")
	userID, _ := util.GetUserID(c)

	_, err := h.service.JoinCommunity(userID, communityID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"message": "Community request sent"})
}

func (h *ParticipantController) LeaveCommunity(c *gin.Context) {
	communityID := c.Param("id")

	userID, _ := util.GetUserID(c)

	err := h.service.LeaveCommunity(userID, communityID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Left community"})
}

func (h *ParticipantController) RejectParticipant(c *gin.Context) {
	communityID := c.Param("id")

	userID, _ := util.GetUserID(c)
	_, err := h.service.RejectPaticipant(userID, communityID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Rejected participant"})
}

func (h *ParticipantController) ApproveParticipant(c *gin.Context) {
	communityID := c.Param("id")
	userID, _ := util.GetUserID(c)
	_, err := h.service.ApproveParticipant(userID, communityID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Approved participant"})
}

func (h *ParticipantController) GetParticipants(c *gin.Context) {
	pageStr := c.DefaultQuery("page", "1")
	limitStr := c.DefaultQuery("limit", "10")

	page, err := strconv.Atoi(pageStr)
	if err != nil || page < 1 {
		page = 1
	}

	limit, err := strconv.Atoi(limitStr)
	if err != nil || limit < 1 {
		limit = 10
	}

	participants, total, err := h.service.GetParticipants(page, limit)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{
		"data":     participants,
		"total":    total,
		"page":     page,
		"limit":    limit,
		"has_more": int64(page*limit) < total,
	})

}
