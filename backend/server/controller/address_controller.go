package controller

import (
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
)

type AddressController struct {
	addressService service.AddressService
}

func NewAddressController(addressService service.AddressService) *AddressController {
	return &AddressController{addressService}
}

func (ac *AddressController) CreateProvince(c *gin.Context) {

}
