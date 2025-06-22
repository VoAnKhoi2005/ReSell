package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	request "github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/gin-gonic/gin"
	"net/http"
	"strconv"
)

type TransactionController struct {
	service service.TransactionService
}

func NewTransactionController(s service.TransactionService) *TransactionController {
	return &TransactionController{service: s}
}

func (tc *TransactionController) GetTransactions(c *gin.Context) {
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

	posts, total, err := tc.service.GetTransactions(page, limit)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{
		"data":     posts,
		"total":    total,
		"page":     page,
		"limit":    limit,
		"has_more": int64(page*limit) < total,
	})
}

func (tc *TransactionController) CreateTransaction(c *gin.Context) {
	var req request.CreateTransactionRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request"})
		return
	}

	clientSecret, transactionID, err := tc.service.CreateTransaction(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{
		"client_secret":  clientSecret,
		"transaction_id": transactionID,
	})
}
