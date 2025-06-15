package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type CartItemRepository interface {
	GetAll() ([]*model.CartItem, error)
	Create(item *model.CartItem) error
	Update(item *model.CartItem) error
	Delete(item *model.CartItem) error

	GetByUserID(userID string) ([]*model.CartItem, error)
	GetByUserIDAndPostID(userID string, postID string) (*model.CartItem, error)
}

type cartItemRepository struct {
	*BaseRepository[model.CartItem] // embed
}

func NewCartItemRepository(db *gorm.DB) CartItemRepository {
	return &cartItemRepository{
		BaseRepository: NewBaseRepository[model.CartItem](db),
	}
}

func (r *cartItemRepository) GetByUserID(userID string) ([]*model.CartItem, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var items []*model.CartItem
	err := r.db.WithContext(ctx).Where("user_id = ?", userID).Find(&items).Error
	return items, err
}

func (r *cartItemRepository) GetByUserIDAndPostID(userID string, postID string) (*model.CartItem, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var item model.CartItem
	err := r.db.WithContext(ctx).Where("user_id = ? AND post_id = ?", userID, postID).First(&item).Error
	if err != nil {
		return nil, err
	}
	return &item, nil
}
