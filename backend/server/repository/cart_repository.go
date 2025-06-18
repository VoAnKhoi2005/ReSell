package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type CartItemRepository interface {
	GetAll() ([]*model.FavoritePosts, error)
	Create(item *model.FavoritePosts) error
	Update(item *model.FavoritePosts) error
	Delete(item *model.FavoritePosts) error

	GetByUserID(userID string) ([]*model.FavoritePosts, error)
	GetByUserIDAndPostID(userID string, postID string) (*model.FavoritePosts, error)
}

type cartItemRepository struct {
	*BaseRepository[model.FavoritePosts] // embed
}

func NewCartItemRepository(db *gorm.DB) CartItemRepository {
	return &cartItemRepository{
		BaseRepository: NewBaseRepository[model.FavoritePosts](db),
	}
}

func (r *cartItemRepository) GetByUserID(userID string) ([]*model.FavoritePosts, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var items []*model.FavoritePosts
	err := r.db.WithContext(ctx).Where("user_id = ?", userID).Find(&items).Error
	return items, err
}

func (r *cartItemRepository) GetByUserIDAndPostID(userID string, postID string) (*model.FavoritePosts, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var item model.FavoritePosts
	err := r.db.WithContext(ctx).Where("user_id = ? AND post_id = ?", userID, postID).First(&item).Error
	if err != nil {
		return nil, err
	}
	return &item, nil
}
