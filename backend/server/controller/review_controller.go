package controller

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/VoAnKhoi2005/ReSell/util"
	"github.com/gin-gonic/gin"
	"net/http"
)

type ReviewController struct {
	reviewService service.ReviewService
}

func NewReviewController(reviewService service.ReviewService) *ReviewController {
	return &ReviewController{reviewService: reviewService}
}

func (rc *ReviewController) CreateReview(c *gin.Context) {
	var request transaction.CreateReviewRequest
	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	userId, err := util.GetUserID(c)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
		return
	}

	review := model.UserReview{
		UserId:  &userId,
		OrderId: &request.OrderID,
		Rating:  request.Rating,
		Comment: request.Comment,
	}

	err = rc.reviewService.CreateReview(&review)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (rc *ReviewController) GetReviewsByBuyerID(c *gin.Context) {
	userID := c.Param("buyer_id")
	if userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "buyer id is required"})
		return
	}

	reviews, err := rc.reviewService.GetReviewByBuyerID(userID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"reviews": reviews})
}

func (rc *ReviewController) GetReviewByOrderID(c *gin.Context) {
	orderID := c.Param("order_id")
	if orderID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "order id is required"})
		return
	}

	review, err := rc.reviewService.GetReviewByOrderID(orderID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"review": review})
}

func (rc *ReviewController) GetReviewByPostID(c *gin.Context) {
	postID := c.Param("post_id")
	if postID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "post id is required"})
		return
	}

	review, err := rc.reviewService.GetReviewByPostID(postID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"review": review})
}

func (rc *ReviewController) DeleteReviewByOrderID(c *gin.Context) {
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

	err = rc.reviewService.DeleteReviewByOrderID(userID, orderID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}
