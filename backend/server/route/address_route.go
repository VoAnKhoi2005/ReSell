package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterAddressRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	addressRepo := repository.NewAddressRepository(db)
	addressService := service.NewAddressService(addressRepo)
	addressContoller := controller.NewAddressController(addressService)

	addressRoute := rg.Group("/address")
	addressRoute.POST("/province/:name", addressContoller.CreateProvince)
}
