package controller

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	request "github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"net/http"
	"strconv"
	"strings"
)

type PostController struct {
	service service.PostService
}

func NewPostController(service service.PostService) *PostController {
	return &PostController{service: service}
}

func (h *PostController) GetAdminPosts(c *gin.Context) {

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

	type Filter struct {
		Status     string
		MinPrice   *uint
		MaxPrice   *uint
		ProvinceID string
		DistrictID string
		WardID     string
		UserID     string
		CategoryID string
	}

	var filter Filter

	// Validate status
	if s := c.Query("status"); s != "" {
		validStatus := map[string]bool{
			"approved": true,
			"rejected": true,
			"hidden":   true,
			"pending":  true,
			"sold":     true,
		}
		if !validStatus[s] {
			c.JSON(http.StatusBadRequest, gin.H{"error": "invalid status"})
			return
		}
		filter.Status = s
	}

	// Min/Max price
	if minVal := c.Query("min_price"); minVal != "" {
		if val, err := strconv.ParseUint(minVal, 10, 64); err == nil {
			v := uint(val)
			filter.MinPrice = &v
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": "min_price must be a number"})
			return
		}
	}
	if maxVal := c.Query("max_price"); maxVal != "" {
		if val, err := strconv.ParseUint(maxVal, 10, 64); err == nil {
			v := uint(val)
			filter.MaxPrice = &v
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": "max_price must be a number"})
			return
		}
	}

	// Validate UUID fields
	validateUUID := func(param string, dest *string) bool {
		if val := c.Query(param); val != "" {
			if _, err := uuid.Parse(val); err != nil {
				c.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("%s must be a valid UUID", param)})
				return false
			}
			*dest = val
		}
		return true
	}

	if !validateUUID("province_id", &filter.ProvinceID) {
		return
	}
	if !validateUUID("district_id", &filter.DistrictID) {
		return
	}
	if !validateUUID("ward_id", &filter.WardID) {
		return
	}
	if !validateUUID("user_id", &filter.UserID) {
		return
	}
	if !validateUUID("category_id", &filter.CategoryID) {
		return
	}

	// Chuyển sang map để truyền xuống repo
	filters := map[string]string{}
	if filter.Status != "" {
		filters["status"] = filter.Status
	}
	if filter.MinPrice != nil {
		filters["min_price"] = fmt.Sprint(*filter.MinPrice)
	}
	if filter.MaxPrice != nil {
		filters["max_price"] = fmt.Sprint(*filter.MaxPrice)
	}
	if filter.ProvinceID != "" {
		filters["province_id"] = filter.ProvinceID
	}
	if filter.DistrictID != "" {
		filters["district_id"] = filter.DistrictID
	}
	if filter.WardID != "" {
		filters["ward_id"] = filter.WardID
	}
	if filter.UserID != "" {
		filters["user_id"] = filter.UserID
	}
	if filter.CategoryID != "" {
		filters["category_id"] = filter.CategoryID
	}

	posts, total, err := h.service.GetAdminPosts(filters, page, limit)
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

func (h *PostController) GetUserPosts(c *gin.Context) {
	ownerID, _ := util.GetUserID(c)

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

	type Filter struct {
		Status      string
		MinPrice    *uint
		MaxPrice    *uint
		ProvinceID  string
		DistrictID  string
		WardID      string
		UserID      string
		CategoryID  string
		Q           string
		IsFollowing string
		IsFavorite  string
	}

	var filter Filter

	//validate q
	if q := c.Query("q"); q != "" {
		filter.Q = q
	}

	if isFavorite := c.Query("is_favorite"); isFavorite != "" {
		filter.IsFavorite = isFavorite
	}

	if isFollowing := c.Query("is_following"); isFollowing != "" {
		filter.IsFollowing = isFollowing
	}

	// Validate status
	if s := c.Query("status"); s != "" {
		validStatus := map[string]bool{
			"approved": true,
			"rejected": true,
			"hidden":   true,
			"pending":  true,
			"sold":     true,
		}
		if !validStatus[s] {
			c.JSON(http.StatusBadRequest, gin.H{"error": "invalid status"})
			return
		}
		filter.Status = s
	}

	// Min/Max price
	if minVal := c.Query("min_price"); minVal != "" {
		if val, err := strconv.ParseUint(minVal, 10, 64); err == nil {
			v := uint(val)
			filter.MinPrice = &v
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": "min_price must be a number"})
			return
		}
	}
	if maxVal := c.Query("max_price"); maxVal != "" {
		if val, err := strconv.ParseUint(maxVal, 10, 64); err == nil {
			v := uint(val)
			filter.MaxPrice = &v
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": "max_price must be a number"})
			return
		}
	}

	// Validate UUID fields
	validateUUID := func(param string, dest *string) bool {
		if val := c.Query(param); val != "" {
			if _, err := uuid.Parse(val); err != nil {
				c.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("%s must be a valid UUID", param)})
				return false
			}
			*dest = val
		}
		return true
	}

	if !validateUUID("province_id", &filter.ProvinceID) {
		return
	}
	if !validateUUID("district_id", &filter.DistrictID) {
		return
	}
	if !validateUUID("ward_id", &filter.WardID) {
		return
	}
	if !validateUUID("user_id", &filter.UserID) {
		return
	}
	if !validateUUID("category_id", &filter.CategoryID) {
		return
	}

	// Chuyển sang map để truyền xuống repo
	filters := map[string]string{}
	if filter.Status != "" {
		filters["status"] = filter.Status
	}
	if filter.MinPrice != nil {
		filters["min_price"] = fmt.Sprint(*filter.MinPrice)
	}
	if filter.MaxPrice != nil {
		filters["max_price"] = fmt.Sprint(*filter.MaxPrice)
	}
	if filter.ProvinceID != "" {
		filters["province_id"] = filter.ProvinceID
	}
	if filter.DistrictID != "" {
		filters["district_id"] = filter.DistrictID
	}
	if filter.WardID != "" {
		filters["ward_id"] = filter.WardID
	}
	if filter.UserID != "" {
		filters["user_id"] = filter.UserID
	}
	if filter.CategoryID != "" {
		filters["category_id"] = filter.CategoryID
	}
	if filter.Q != "" {
		filters["q"] = filter.Q
	}
	filters["is_following"] = filter.IsFollowing
	filters["is_favorite"] = filter.IsFavorite

	posts, total, err := h.service.GetUserPosts(ownerID, filters, page, limit)
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

func (h *PostController) GetPostsByIdList(c *gin.Context) {
	ownerID, _ := util.GetUserID(c)

	// Parse IDs từ query string
	idsParam := c.Query("ids")
	if idsParam == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ids parameter is required"})
		return
	}
	ids := strings.Split(idsParam, ",")

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

	posts, total, err := h.service.GetPostsByIdList(ownerID, ids, page, limit)
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

func (h *PostController) GetPostByID(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.GetPostByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, post)
}

func (h *PostController) CreatePost(c *gin.Context) {
	var req request.CreatePostRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	userID, _ := util.GetUserID(c)

	post, err := h.service.CreatePost(&req, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, post)
}

func (h *PostController) UpdatePost(c *gin.Context) {
	var req request.UpdatePostRequest

	id := c.Param("id")

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	post, err := h.service.UpdatePost(id, &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"id": post.ID, "message": "Post updated"})

}

func (h *PostController) DeletePost(c *gin.Context) {
	id := c.Param("id")
	err := h.service.DeletePost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Post deleted permanently"})
}

func (h *PostController) ApprovePost(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.ApprovePost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post approved"})
}

func (h *PostController) RejectPost(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.RejectPost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post rejected"})

}

func (h *PostController) MarkPostAsDeleted(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.MarkPostAsDeleted(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post marked as deleted"})
}

func (h *PostController) RestoreDeletedPost(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.RestoreDeletedPost(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post restored"})
}

func (h *PostController) MarkPostAsSold(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.MarkPostAsSold(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post marked as sold"})

}

func (h *PostController) RevertSoldStatus(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.RevertSoldStatus(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, gin.H{"id": post.ID, "message": "Post sold status reverted"})
}

func (h *PostController) GetAllDeletedPosts(c *gin.Context) {
	posts, err := h.service.GetAllDeletedPosts()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, posts)
}

func (h *PostController) GetDeletedPostByID(c *gin.Context) {
	id := c.Param("id")
	post, err := h.service.GetDeletedPostByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, post)
}

func (h *PostController) UploadPostImages(c *gin.Context) {
	postId := c.Param("id")
	form, err := c.MultipartForm()
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid multipart form", "details": err.Error()})
		return
	}

	files := form.File["images"]
	if len(files) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "No images provided"})
		return
	}

	var imageUrls []string

	for _, fileHeader := range files {
		// Mở từng file
		file, err := fileHeader.Open()
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("Failed to open file %s", fileHeader.Filename)})
			return
		}

		// Upload lên Cloudinary
		imageURL, err := util.UploadToCloudinary(file, fileHeader)
		file.Close() // Đóng file sau khi dùng
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error":   fmt.Sprintf("Failed to upload image %s", fileHeader.Filename),
				"details": err.Error(),
			})
			return
		}

		imageUrls = append(imageUrls, imageURL)

		// Lưu vào database
		_, err = h.service.CreatePostImage(postId, imageURL)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error":   fmt.Sprintf("Failed to save image %s to DB", fileHeader.Filename),
				"details": err.Error(),
			})
			return
		}
	}

	c.JSON(http.StatusOK, gin.H{
		"image_urls": imageUrls,
		"message":    "Images uploaded successfully",
	})
}

func (h *PostController) UploadPostImage(c *gin.Context) {
	postID := c.Param("id")
	file, fileHeader, err := c.Request.FormFile("image")
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Missing image file"})
		return
	}

	defer file.Close()

	imageURL, err := util.UploadToCloudinary(file, fileHeader)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Upload failed", "details": err.Error()})
		return
	}

	postImage, err := h.service.CreatePostImage(postID, imageURL) // Order is not used here

	c.JSON(http.StatusOK, gin.H{"post_image": postImage, "message": "Image uploaded successfully"})
}

func (h *PostController) DeletePostImages(c *gin.Context) {
	postID := c.Param("id")

	var req struct {
		ImageURLs []string `json:"image_urls"`
	}
	if err := c.ShouldBindJSON(&req); err != nil || len(req.ImageURLs) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid image_urls"})
		return
	}

	for _, imageURL := range req.ImageURLs {
		// 1. Tách public_id từ URL
		publicID := util.ExtractPublicID(imageURL)

		// 2. Xóa Cloudinary
		err := util.DeleteFromCloudinary(publicID)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": fmt.Sprintf("failed to delete image from Cloudinary: %s", err.Error())})
			return
		}

		// 3. Xóa trong DB
		err = h.service.DeletePostImage(postID, imageURL)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": fmt.Sprintf("failed to delete image from database: %s", err.Error())})
			return
		}
	}

	c.JSON(http.StatusOK, gin.H{"message": "Images deleted"})

}

func (h *PostController) GetOwnPosts(c *gin.Context) {
	userID, _ := util.GetUserID(c)

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

	type Filter struct {
		Status     string
		MinPrice   *uint
		MaxPrice   *uint
		ProvinceID string
		DistrictID string
		WardID     string
		CategoryID string
		Q          string
	}

	var filter Filter

	//validate q
	if q := c.Query("q"); q != "" {
		filter.Q = q
	}

	// Validate status
	if s := c.Query("status"); s != "" {
		validStatus := map[string]bool{
			"approved": true,
			"rejected": true,
			"hidden":   true,
			"pending":  true,
			"sold":     true,
		}
		if !validStatus[s] {
			c.JSON(http.StatusBadRequest, gin.H{"error": "invalid status"})
			return
		}
		filter.Status = s
	}

	// Min/Max price
	if minVal := c.Query("min_price"); minVal != "" {
		if val, err := strconv.ParseUint(minVal, 10, 64); err == nil {
			v := uint(val)
			filter.MinPrice = &v
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": "min_price must be a number"})
			return
		}
	}
	if maxVal := c.Query("max_price"); maxVal != "" {
		if val, err := strconv.ParseUint(maxVal, 10, 64); err == nil {
			v := uint(val)
			filter.MaxPrice = &v
		} else {
			c.JSON(http.StatusBadRequest, gin.H{"error": "max_price must be a number"})
			return
		}
	}

	// Validate UUID fields
	validateUUID := func(param string, dest *string) bool {
		if val := c.Query(param); val != "" {
			if _, err := uuid.Parse(val); err != nil {
				c.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("%s must be a valid UUID", param)})
				return false
			}
			*dest = val
		}
		return true
	}

	if !validateUUID("province_id", &filter.ProvinceID) {
		return
	}
	if !validateUUID("district_id", &filter.DistrictID) {
		return
	}
	if !validateUUID("ward_id", &filter.WardID) {
		return
	}
	if !validateUUID("category_id", &filter.CategoryID) {
		return
	}

	// Chuyển sang map để truyền xuống repo
	filters := map[string]string{}
	if filter.Status != "" {
		filters["status"] = filter.Status
	}
	if filter.MinPrice != nil {
		filters["min_price"] = fmt.Sprint(*filter.MinPrice)
	}
	if filter.MaxPrice != nil {
		filters["max_price"] = fmt.Sprint(*filter.MaxPrice)
	}
	if filter.ProvinceID != "" {
		filters["province_id"] = filter.ProvinceID
	}
	if filter.DistrictID != "" {
		filters["district_id"] = filter.DistrictID
	}
	if filter.WardID != "" {
		filters["ward_id"] = filter.WardID
	}
	if filter.CategoryID != "" {
		filters["category_id"] = filter.CategoryID
	}
	if filter.Q != "" {
		filters["q"] = filter.Q
	}

	posts, total, err := h.service.GetOwnPosts(userID, filters, page, limit)
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

func (h *PostController) IsSold(c *gin.Context) {
	postID := c.Param("id")

	if postID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "post id is required"})
		return
	}

	isSold, err := h.service.IsSold(postID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, isSold)
}
