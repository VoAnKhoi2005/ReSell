package controller

import (
	"encoding/json"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"github.com/stripe/stripe-go/v78"
	"github.com/stripe/stripe-go/v78/webhook"
	"io/ioutil"
	"net/http"
	"os"
)

type StripeController struct {
	service service.StripeService
}

func NewStripeController(service service.StripeService) *StripeController {
	return &StripeController{
		service: service,
	}
}

type TransferRequest struct {
	UserID string `json:"user_id"`
	Amount int64  `json:"amount"` // tính bằng đơn vị nhỏ nhất: VND = đồng
}

func (h *StripeController) CreateExpressAccount(c *gin.Context) {
	userID := c.Param("id")

	accountID, err := h.service.CreateExpressAccount(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"account_id": accountID})
}

func (h *StripeController) GetOnboardingLink(c *gin.Context) {
	userID := c.Param("id")

	linkURL, err := h.service.GetOnboardingLink(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"url": linkURL})
}

func (h *StripeController) TransferToSeller(c *gin.Context) {
	var req TransferRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err := h.service.TransferToSeller(req.UserID, req.Amount)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Transferred successfully"})
}

func (h *StripeController) HandleStripeWebhook(c *gin.Context) {
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
		err := h.service.MarkTransactionSuccess(intent.ID)
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
		err := h.service.MarkTransactionFailed(intent.ID, intent.LastPaymentError.Msg)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update failed transaction"})
			return
		}
	case "account.updated":
		var account stripe.Account
		if err := json.Unmarshal(event.Data.Raw, &account); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Unable to parse account data"})
			return
		}

		// Nếu tài khoản đã được xác minh
		if account.ChargesEnabled {
			err := h.service.MarkStripeVerified(account.ID)
			if err != nil {
				c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to mark user verified"})
				return
			}
		}

	}

	c.Status(http.StatusOK)
}

func (h *StripeController) ReturnAfterCreateExpressAccount(c *gin.Context) {
	c.Header("Content-Type", "text/html; charset=utf-8")
	c.String(http.StatusOK, `
		<!DOCTYPE html>
		<html>
		<head>
			<title>Đăng ký thành công</title>
			<style>
				body {
					font-family: sans-serif;
					display: flex;
					justify-content: center;
					align-items: center;
					height: 100vh;
					background-color: #f5f5f5;
				}
				.message {
					background: white;
					padding: 2rem;
					border-radius: 8px;
					box-shadow: 0 4px 8px rgba(0,0,0,0.1);
					text-align: center;
				}
			</style>
		</head>
		<body>
			<div class="message">
				<h1>✅ Đăng ký tài khoản Stripe thành công!</h1>
				<p>Bạn có thể quay lại ứng dụng để tiếp tục sử dụng.</p>
			</div>
		</body>
		</html>
	`)
}

func (h *StripeController) RefreshAfterCreateExpressAccount(c *gin.Context) {
	c.String(http.StatusOK, "Đăng ký tài khoản bán hàng thất bại hoặc bị hủy. Vui lòng thử lại.")
}
