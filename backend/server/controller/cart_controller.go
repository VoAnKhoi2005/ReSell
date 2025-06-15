package controller

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"net/http"
)

type CartController struct {
	cartService service.CartService
	postService service.PostService
}

func NewCartController(cartService service.CartService, postService service.PostService) *CartController {
	return &CartController{
		cartService: cartService,
		postService: postService,
	}
}

func (cart *CartController) GetCartItems(c *gin.Context) {
	userID, err := util.GetUserID(c)
	items, err := cart.cartService.GetCartItems(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get cart items"})
		return
	}
	c.JSON(http.StatusOK, items)
}

func (cart *CartController) CreateCartItem(c *gin.Context) {
	userID, _ := util.GetUserID(c)

	if !util.IsUserOwner(c, userID) {
		return
	}

	var req transaction.CreateCartRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request"})
		return
	}

	post, err := cart.postService.GetPostByID(req.PostID)
	if *post.UserID == userID {
		c.JSON(http.StatusBadRequest, gin.H{"error": "You cannot add your own post to the cart"})
		return
	}

	item, err := cart.cartService.AddItemToCart(userID, req.PostID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to add item to cart"})
		return
	}
	c.JSON(http.StatusCreated, gin.H{"message": "Item added to cart", "item": item})
}

func (cart *CartController) DeleteCartItem(c *gin.Context) {
	userID, _ := util.GetUserID(c)

	if !util.IsUserOwner(c, userID) {
		return
	}

	postID := c.Param("post_id")

	if postID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Post ID is required"})
		return
	}

	err := cart.cartService.RemoveItemFromCart(userID, postID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to remove item from cart"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Item removed from cart"})
}
