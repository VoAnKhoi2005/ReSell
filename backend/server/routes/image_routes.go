package routes

import (
	controller "github.com/VoAnKhoi2005/ReSell/controllers"
	"github.com/gin-gonic/gin"
)

func RegisterImageRoutes(r *gin.Engine) {
	r.POST("/upload-image", controller.UploadImageHandler)
}
