package recommender

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"net/http"
	"strconv"
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

func (rc *Controller) GetCandidatePostsID(c *gin.Context) {
	pageSizeStr := c.DefaultQuery("pageSize", "10")
	pageSize, err := strconv.Atoi(pageSizeStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	pageStr := c.DefaultQuery("page", "1")
	page, err := strconv.Atoi(pageStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if pageSize > 200 || pageSize < 10 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "pageSize is too large or too small"})
		return
	}

	if page < 1 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "page must be greater than zero"})
		return
	}

	postsID, err := rc.service.GetCandidatePostsID(page, pageSize)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, postsID)
}

func (rc *Controller) GetRecommendation(c *gin.Context) {
	userID, err := util.GetUserID(c)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	pageSizeStr := c.DefaultQuery("pageSize", "10")
	pageSize, err := strconv.Atoi(pageSizeStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	pageStr := c.DefaultQuery("page", "1")
	page, err := strconv.Atoi(pageStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if pageSize > 200 || pageSize < 10 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "pageSize is too large or too small"})
		return
	}

	if page < 1 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "page must be greater than zero"})
		return
	}

	posts, err := rc.service.GetRecommendation(userID, pageSize, page)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, posts)
}
