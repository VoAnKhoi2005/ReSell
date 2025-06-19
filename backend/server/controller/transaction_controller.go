package controller

import (
	"encoding/json"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	request "github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/gin-gonic/gin"
	"github.com/stripe/stripe-go/v78"
	"github.com/stripe/stripe-go/v78/webhook"
	"io/ioutil"
	"net/http"
	"os"
)

type TransactionController struct {
	service service.TransactionService
}

func NewTransactionController(s service.TransactionService) *TransactionController {
	return &TransactionController{service: s}
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

func (tc *TransactionController) HandleStripeWebhook(c *gin.Context) {
	const MaxBodyBytes = int64(65536)
	c.Request.Body = http.MaxBytesReader(c.Writer, c.Request.Body, MaxBodyBytes)

	payload, err := ioutil.ReadAll(c.Request.Body)

	fmt.Println("Webhook received:", string(payload))

	if err != nil {
		c.JSON(http.StatusServiceUnavailable, gin.H{"error": "Unable to read body"})
		return
	}

	endpointSecret := os.Getenv("STRIPE_WEBHOOK_SECRET")
	sigHeader := c.GetHeader("Stripe-Signature")

	event, err := webhook.ConstructEventWithOptions(payload, sigHeader, endpointSecret, webhook.ConstructEventOptions{
		IgnoreAPIVersionMismatch: true,
	})

	if err != nil {
		fmt.Println(">>> Signature error:", err)
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid signature"})
		return
	}

	switch event.Type {
	case "payment_intent.succeeded":
		var intent stripe.PaymentIntent
		if err := json.Unmarshal(event.Data.Raw, &intent); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Unable to parse payment intent"})
			return
		}
		err := tc.service.MarkTransactionSuccess(intent.ID)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update transaction"})
			return
		}

	case "payment_intent.payment_failed":
		var intent stripe.PaymentIntent
		if err := json.Unmarshal(event.Data.Raw, &intent); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Unable to parse failed payment intent"})
			return
		}
		err := tc.service.MarkTransactionFailed(intent.ID, intent.LastPaymentError.Msg)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update failed transaction"})
			return
		}
	}

	c.Status(http.StatusOK)
}
