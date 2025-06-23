package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"net/http"
	"strconv"
)

type ReportController struct {
	service service.ReportService
}

func NewReportController(service service.ReportService) *ReportController {
	return &ReportController{
		service: service,
	}
}

func (h *ReportController) GetReportUsers(c *gin.Context) {

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

	reports, total, err := h.service.GetReportUserPaginated(page, limit)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{
		"data":     reports,
		"total":    total,
		"page":     page,
		"limit":    limit,
		"has_more": int64(page*limit) < total,
	})
}

func (h *ReportController) GetReportPosts(c *gin.Context) {

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

	reports, total, err := h.service.GetReportPostPaginated(page, limit)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{
		"data":     reports,
		"total":    total,
		"page":     page,
		"limit":    limit,
		"has_more": int64(page*limit) < total,
	})
}

func (h *ReportController) CreateReportUser(c *gin.Context) {
	userID, _ := util.GetUserID(c)
	var req transaction.ReportUserRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err := h.service.CreateReportUser(&req, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, gin.H{"message": "Created report user successfully!"})
}

func (h *ReportController) CreateReportPost(c *gin.Context) {
	userID, _ := util.GetUserID(c)
	var req transaction.ReportPostRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err := h.service.CreateReportPost(&req, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusCreated, gin.H{"message": "Created report post successfully!"})
}
