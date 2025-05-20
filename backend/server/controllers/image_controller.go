package controllers

import (
	"github.com/VoAnKhoi2005/ReSell/utils"
	"github.com/gin-gonic/gin"
	"net/http"
)

func UploadImageHandler(c *gin.Context) {
	file, fileHeader, err := c.Request.FormFile("image")
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Missing image file"})
		return
	}

	defer file.Close()

	imageURL, err := utils.UploadToCloudinary(file, fileHeader)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Upload failed", "details": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"image_url": imageURL})
}
