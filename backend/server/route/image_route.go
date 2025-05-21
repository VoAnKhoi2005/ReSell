package route

import (
	controller "github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/gin-gonic/gin"
)

func RegisterImageRoutes(rg *gin.RouterGroup) {
	images := rg.Group("/image")

	images.POST("/upload-image", controller.UploadImageHandler)

}
