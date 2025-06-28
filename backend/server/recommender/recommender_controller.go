package recommender

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

type Controller struct {
	service Service
}

func NewRecommenderController(service Service) *Controller {
	return &Controller{service}
}

func (rc *Controller) GetBuyerProfile(c *gin.Context) {
	userID := c.Param("user_id")
	if userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "user_id required"})
		return
	}

	profile, err := rc.service.GetBuyerProfile(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, profile)
}

type GetPostsFeaturesRequest struct {
	UserID  string   `json:"user_id" binding:"required"`
	PostsID []string `json:"posts_id" binding:"required"`
}

func (rc *Controller) GetPostsFeatures(c *gin.Context) {
	var request GetPostsFeaturesRequest
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	postsFeatures, err := rc.service.GetPostsFeatures(request.PostsID, request.UserID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, postsFeatures)
}
