package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/gin-gonic/gin"
	"net/http"
	"strconv"
)

type CommunityController struct {
	service service.CommunityService
}

func NewCommunityController(service service.CommunityService) *CommunityController {
	return &CommunityController{service: service}
}

func (h *CommunityController) GetCommunities(c *gin.Context) {
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

	communities, total, err := h.service.GetCommunities(page, limit)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{
		"data":     communities,
		"total":    total,
		"page":     page,
		"limit":    limit,
		"has_more": int64(page*limit) < total,
	})

}

func (h *CommunityController) GetCommunityByID(c *gin.Context) {
	id := c.Param("id")
	community, err := h.service.GetCommunityByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, community)
}

func (h *CommunityController) CreateCommunity(c *gin.Context) {
	var req transaction.CreateCommunityRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	community, err := h.service.CreateCommunity(&req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": community.ID, "message": "Community created"})
}

func (h *CommunityController) UpdateCommunity(c *gin.Context) {
	id := c.Param("id")
	var req transaction.UpdateCommunityRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	community, err := h.service.UpdateCommunity(id, &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"id": community.ID, "message": "Community updated"})
}

func (h *CommunityController) DeleteCommunity(c *gin.Context) {
	id := c.Param("id")
	err := h.service.DeleteCommunity(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Community deleted"})
}

func (h *CommunityController) ApproveCommunity(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.ApproveCommunity(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Community approved"})
}

func (h *CommunityController) RejectCommunity(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.RejectCommunity(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Community rejected"})

}
