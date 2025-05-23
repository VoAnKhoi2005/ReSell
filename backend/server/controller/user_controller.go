package controller

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/VoAnKhoi2005/ReSell/util"
	"github.com/gin-gonic/gin"
	"net/http"
)

type UserController struct {
	userService    service.UserService
	addressService service.AddressService
}

func NewUserController(userService service.UserService, addressService service.AddressService) *UserController {
	return &UserController{
		userService:    userService,
		addressService: addressService,
	}
}

func (h *UserController) GetUserByID(c *gin.Context) {
	userID := c.Param("id")

	user, err := h.userService.GetUserByID(userID)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"user": user})
}

func (h *UserController) UpdateUser(c *gin.Context) {
	var user *model.User

	err := c.ShouldBind(&user)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if !util.IsUserOwner(c, &user.ID) {
		return
	}

	err = h.userService.UpdateUser(user)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (h *UserController) DeleteUser(c *gin.Context) {
	userID := c.Param("id")

	if !util.IsUserOwner(c, &userID) {
		return
	}

	err := h.userService.DeleteUserByID(userID)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (h *UserController) Follow(c *gin.Context) {
	var request transaction.FollowRequest
	err := c.ShouldBind(&request)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	follower, err := h.userService.GetUserByID(*request.FollowerID)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	if !util.IsUserOwner(c, request.FollowerID) {
		return
	}

	followee, err := h.userService.GetUserByID(*request.FolloweeID)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	err = h.userService.FollowUser(&follower.ID, &followee.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}

func (h *UserController) AddAddress(c *gin.Context) {
	var address *model.Address
	err := c.ShouldBind(&address)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if !util.IsUserOwner(c, address.UserID) {
		return
	}

	err = h.addressService.Create(address)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"success": true})
}
