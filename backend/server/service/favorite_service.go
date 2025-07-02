package service

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
)

type FavoriteService interface {
	AddItemToCart(userID string, postID string) (*model.FavoritePost, error)
	RemoveItemFromCart(userID string, postID string) error
	GetCartItems(userID string) ([]*model.FavoritePost, error)
}

type favoriteService struct {
	repo repository.FavouriteRepository // Assuming CartRepository is defined elsewhere
}

func NewFavoriteService(repo repository.FavouriteRepository) FavoriteService {
	return &favoriteService{
		repo: repo,
	}
}

func (s *favoriteService) AddItemToCart(userID string, postID string) (*model.FavoritePost, error) {
	cartItem := &model.FavoritePost{
		UserID: &userID,
		PostID: &postID,
	}

	err := s.repo.Create(cartItem)
	return cartItem, err
}

func (s *favoriteService) RemoveItemFromCart(userID string, postID string) error {
	cartItem, err := s.repo.GetByUserIDAndPostID(userID, postID)
	if err != nil {
		return err
	}

	return s.repo.Delete(cartItem)
}

func (s *favoriteService) GetCartItems(userID string) ([]*model.FavoritePost, error) {
	return s.repo.GetByUserID(userID)
}
