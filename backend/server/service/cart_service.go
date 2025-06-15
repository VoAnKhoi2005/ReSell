package service

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
)

type CartService interface {
	AddItemToCart(userID string, postID string) (*model.CartItem, error)
	RemoveItemFromCart(userID string, postID string) error
	GetCartItems(userID string) ([]*model.CartItem, error)
}

type cartService struct {
	repo repository.CartItemRepository // Assuming CartRepository is defined elsewhere
}

func NewCartService(repo repository.CartItemRepository) CartService {
	return &cartService{
		repo: repo,
	}
}

func (s *cartService) AddItemToCart(userID string, postID string) (*model.CartItem, error) {
	cartItem := &model.CartItem{
		UserID: &userID,
		PostID: &postID,
	}

	err := s.repo.Create(cartItem)
	return cartItem, err
}

func (s *cartService) RemoveItemFromCart(userID string, postID string) error {
	cartItem, err := s.repo.GetByUserIDAndPostID(userID, postID)
	if err != nil {
		return err
	}

	return s.repo.Delete(cartItem)
}

func (s *cartService) GetCartItems(userID string) ([]*model.CartItem, error) {
	return s.repo.GetByUserID(userID)
}
