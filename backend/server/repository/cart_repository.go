package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type CartItemRepository interface {
	GetAll() ([]*model.FavoritePost, error)
	Create(item *model.FavoritePost) error
	Update(item *model.FavoritePost) error
	Delete(item *model.FavoritePost) error

	GetByUserID(userID string) ([]*model.FavoritePost, error)
	GetByUserIDAndPostID(userID string, postID string) (*model.FavoritePost, error)
}

type cartItemRepository struct {
	*BaseRepository[model.FavoritePost] // embed
}

func NewCartItemRepository(db *gorm.DB) CartItemRepository {
	return &cartItemRepository{
		BaseRepository: NewBaseRepository[model.FavoritePost](db),
	}
}

func (r *cartItemRepository) GetByUserID(userID string) ([]*model.FavoritePost, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var items []*model.FavoritePost
	err := r.db.WithContext(ctx).Where("user_id = ?", userID).Find(&items).Error
	return items, err
}

func (r *cartItemRepository) GetByUserIDAndPostID(userID string, postID string) (*model.FavoritePost, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var item model.FavoritePost
	err := r.db.WithContext(ctx).Where("user_id = ? AND post_id = ?", userID, postID).First(&item).Error
	if err != nil {
		return nil, err
	}
	return &item, nil
}
