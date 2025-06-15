package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type OrderRepository interface {
	GetAll() ([]*model.ShopOrder, error)
	Create(order *model.ShopOrder) error
	Update(order *model.ShopOrder) error
	Delete(order *model.ShopOrder) error

	GetByID(orderID string) (*model.ShopOrder, error)
	GetByIDs(orderIDs []string) ([]*model.ShopOrder, error)
	GetByPostID(postID string) (*model.ShopOrder, error)
	GetByBuyerID(buyerID string) ([]*model.ShopOrder, error)
	GetBySellerID(SellerID string) ([]*model.ShopOrder, error)
}

type oderRepository struct {
	*BaseRepository[model.ShopOrder]
}

func NewOrderRepository(db *gorm.DB) OrderRepository {
	return &oderRepository{BaseRepository: NewBaseRepository[model.ShopOrder](db)}
}

func (o *oderRepository) GetByID(orderID string) (*model.ShopOrder, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var order *model.ShopOrder = nil
	err := o.db.WithContext(ctx).First(&order, "id = ?", orderID).Error
	return order, err
}

func (o *oderRepository) GetByIDs(orderIDs []string) ([]*model.ShopOrder, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var orders []*model.ShopOrder = nil
	err := o.db.WithContext(ctx).Find(&orders, "id IN ?", orderIDs).Error
	return orders, err
}

func (o *oderRepository) GetByPostID(postID string) (*model.ShopOrder, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var order *model.ShopOrder = nil
	err := o.db.WithContext(ctx).First(&order, "post_id = ?", postID).Error
	return order, err
}

func (o *oderRepository) GetByBuyerID(buyerID string) ([]*model.ShopOrder, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var order []*model.ShopOrder = nil
	err := o.db.WithContext(ctx).Find(&order, "user_id = ?", buyerID).Error
	return order, err
}

func (o *oderRepository) GetBySellerID(SellerID string) ([]*model.ShopOrder, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var orders []*model.ShopOrder = nil
	err := o.db.WithContext(ctx).Joins("Post").
		Where("posts.user_id = ?", SellerID).
		Preload("Post").
		Find(&orders).Error
	return orders, err
}
