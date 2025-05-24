package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterAddressRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	addressRepo := repository.NewAddressRepository(db)
	addressService := service.NewAddressService(addressRepo)
	addressController := controller.NewAddressController(addressService)

	addressRoute := rg.Group("/address")
	addressRoute.Use(middleware.AuthMiddleware())

	//User
	addressRoute.POST("/user/:id", addressController.CreateAddress)
	addressRoute.GET("/:address_id", addressController.GetAddressByID)
	addressRoute.GET("/user/:user_id", addressController.GetAddressByUserID)
	addressRoute.PUT("/", addressController.UpdateAddress)
	addressRoute.DELETE("/:address_id", addressController.DeleteAddress)

	addressRoute.GET("/province/all", addressController.GetAllProvinces)
	addressRoute.GET("district/:province_id", addressController.GetDistricts)
	addressRoute.GET("ward/:district_id", addressController.GetWards)

	//Admin
	adminAddressRoute := rg.Group("/address/admin")
	adminAddressRoute.Use(middleware.AdminAuthMiddleware())
	adminAddressRoute.POST("/province", addressController.CreateProvince)
	adminAddressRoute.POST("/provinces", addressController.CreateProvinces)
	adminAddressRoute.POST("/districts", addressController.CreateDistricts)
	adminAddressRoute.POST("/wards", addressController.CreateWards)
	adminAddressRoute.PUT("/province/:id/:new_name", addressController.UpdateProvince)
	adminAddressRoute.PUT("/district/:id/:new_name", addressController.UpdateDistrict)
	adminAddressRoute.PUT("/ward/:id/:new_name", addressController.UpdateWard)
	adminAddressRoute.DELETE("province/:province_id", addressController.DeleteProvince)
	adminAddressRoute.DELETE("district/:district_id", addressController.DeleteDistrict)
	adminAddressRoute.DELETE("ward/:ward_id", addressController.DeleteWard)
}
