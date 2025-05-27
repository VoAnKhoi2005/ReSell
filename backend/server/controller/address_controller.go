package controller

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/VoAnKhoi2005/ReSell/util"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"net/http"
)

type AddressController struct {
	addressService service.AddressService
}

func NewAddressController(addressService service.AddressService) *AddressController {
	return &AddressController{addressService}
}

func (ac *AddressController) CreateAddress(c *gin.Context) {
	var request transaction.CreateAddressRequest
	err := c.ShouldBindJSON(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if !util.IsUserOwner(c, request.UserID) {
		return
	}

	address := model.Address{
		ID:        uuid.New().String(),
		UserID:    &request.UserID,
		WardID:    &request.WardID,
		Detail:    request.Detail,
		IsDefault: request.IsDefault,
	}

	err = ac.addressService.CreateAddress(&address)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) GetAllProvinces(c *gin.Context) {
	provinces, err := ac.addressService.GetAllProvinces()
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"provinces": provinces})
}

func (ac *AddressController) GetDistricts(c *gin.Context) {
	provinceId := c.Param("province_id")
	if provinceId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Province id can't be empty"})
		return
	}

	districts, err := ac.addressService.GetDistricts(provinceId)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"districts": districts})
}

func (ac *AddressController) GetWards(c *gin.Context) {
	districtId := c.Param("district_id")
	if districtId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "District id can't be empty"})
		return
	}

	wards, err := ac.addressService.GetWards(districtId)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"wards": wards})
}

func (ac *AddressController) GetAddressByID(c *gin.Context) {
	addressId := c.Param("address_id")
	if addressId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Address id can't be empty"})
		return
	}

	address, err := ac.addressService.GetByID(addressId)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"address": address})
}

func (ac *AddressController) GetAddressByUserID(c *gin.Context) {
	userId := c.Param("user_id")
	if userId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "User id can't be empty"})
		return
	}

	addresses, err := ac.addressService.GetByUserID(userId)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"addresses": addresses})
}

func (ac *AddressController) UpdateAddress(c *gin.Context) {
	var address *model.Address
	err := c.ShouldBind(&address)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if !util.IsUserOwner(c, *address.UserID) {
		return
	}

	err = ac.addressService.UpdateAddress(address)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) DeleteAddress(c *gin.Context) {
	addressId := c.Param("address_id")
	if addressId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Address id can't be empty"})
		return
	}

	err := ac.addressService.DeleteAddress(addressId)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) CreateProvince(c *gin.Context) {
	provinceName := c.Param("name")
	if provinceName == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Province name can't be empty"})
		return
	}

	province := model.Province{
		Name: provinceName,
	}

	err := ac.addressService.CreateProvince(&province)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) CreateProvinces(c *gin.Context) {
	var provinceNames []string
	if err := c.BindJSON(&provinceNames); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	var failResponses []transaction.FailCreateResponse
	for _, provinceName := range provinceNames {
		province := model.Province{
			Name: provinceName,
		}

		err := ac.addressService.CreateProvince(&province)
		if err != nil {
			failResponses = append(failResponses, transaction.FailCreateResponse{
				Name:    province.Name,
				Message: err.Error(),
			})
		}
	}

	if len(failResponses) > 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": failResponses})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) CreateDistricts(c *gin.Context) {
	var requests []transaction.CreateDistrictRequest
	if err := c.ShouldBindJSON(&requests); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	var failResponses []transaction.FailCreateResponse
	for _, req := range requests {
		district := model.District{
			Name:       req.Name,
			ProvinceID: &req.ProvinceID,
		}
		err := ac.addressService.CreateDistrict(&district)
		if err != nil {
			failResponses = append(failResponses, transaction.FailCreateResponse{
				Name:    district.Name,
				Message: err.Error(),
			})
		}
	}

	if len(failResponses) > 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": failResponses})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) CreateWards(c *gin.Context) {
	var requests []transaction.CreateWardRequest
	if err := c.ShouldBindJSON(&requests); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	var failResponses []transaction.FailCreateResponse
	for _, req := range requests {
		ward := model.Ward{
			Name:       req.Name,
			DistrictID: &req.DistrictID,
		}
		err := ac.addressService.CreateWard(&ward)
		if err != nil {
			failResponses = append(failResponses, transaction.FailCreateResponse{
				Name:    ward.Name,
				Message: err.Error(),
			})
		}
	}

	if len(failResponses) > 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": failResponses})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) UpdateProvince(c *gin.Context) {
	provinceID := c.Param("id")
	newName := c.Param("new_name")
	if provinceID == "" || newName == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid province id or new_name"})
		return
	}

	err := ac.addressService.UpdateProvince(provinceID, newName)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) UpdateDistrict(c *gin.Context) {
	districtID := c.Param("id")
	newName := c.Param("new_name")
	if districtID == "" || newName == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid district id or new_name"})
		return
	}

	err := ac.addressService.UpdateDistrict(districtID, newName)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) UpdateWard(c *gin.Context) {
	wardID := c.Param("id")
	newName := c.Param("new_name")
	if wardID == "" || newName == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid ward id or new_name"})
		return
	}

	err := ac.addressService.UpdateWard(wardID, newName)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) DeleteProvince(c *gin.Context) {
	provinceId := c.Param("province_id")
	if provinceId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Province id can't be empty"})
		return
	}

	err := ac.addressService.DeleteProvince(provinceId)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) DeleteAllProvinces(c *gin.Context) {
	errs := ac.addressService.DeleteProvinces()
	if len(errs) > 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": errs})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) DeleteDistrict(c *gin.Context) {
	districtId := c.Param("district_id")
	if districtId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "District id can't be empty"})
		return
	}

	err := ac.addressService.DeleteDistrict(districtId)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) DeleteDistricts(c *gin.Context) {
	provinceId := c.Param("province_id")
	if provinceId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Province id can't be empty"})
		return
	}

	errs := ac.addressService.DeleteDistricts(provinceId)
	if len(errs) > 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": errs})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) DeleteWard(c *gin.Context) {
	wardId := c.Param("ward_id")
	if wardId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Ward id can't be empty"})
		return
	}

	err := ac.addressService.DeleteWard(wardId)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (ac *AddressController) DeleteWards(c *gin.Context) {
	districtId := c.Param("district_id")
	if districtId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "District id can't be empty"})
		return
	}

	errs := ac.addressService.DeleteWards(districtId)
	if len(errs) > 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": errs})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}
