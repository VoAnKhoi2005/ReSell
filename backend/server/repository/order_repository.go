package repository

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/util"
	"gorm.io/gorm"
)

type OrderRepository interface {
	GetAll() ([]*model.ShopOrder, error)
	Create(order *model.ShopOrder) error
	Update(order *model.ShopOrder) error
	Delete(order *model.ShopOrder) error

	GetByID(orderID string) (*model.ShopOrder, error)
	GetByPostID(PostID string) (*model.ShopOrder, error)
	GetByBuyerID(BuyerID string) (*model.ShopOrder, error)
	GetBySellerID(SellerID string) ([]*model.ShopOrder, error)

	CreateReview(review *model.UserReview) error
	GetReviewByID(reviewID string) (*model.UserReview, error)
	DeleteReview(review *model.UserReview) error
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
	err := o.db.WithContext(ctx).First(&order, orderID).Error
	return order, err
}

func (o *oderRepository) GetByPostID(PostID string) (*model.ShopOrder, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var order *model.ShopOrder = nil
	err := o.db.WithContext(ctx).First(&order, "post_id = ?", PostID).Error
	return order, err
}

func (o *oderRepository) GetByBuyerID(BuyerID string) (*model.ShopOrder, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var order *model.ShopOrder = nil
	err := o.db.WithContext(ctx).First(&order, "user_id = ?", BuyerID).Error
	return order, err
}

func (o *oderRepository) GetBySellerID(SellerID string) ([]*model.ShopOrder, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var orders []*model.ShopOrder = nil
	err := o.db.WithContext(ctx).
		Joins("Post").
		Preload("Post").
		Where("posts.user_id = ?", SellerID).
		Find(&orders).Error
	if err != nil {
		return nil, err
	}

	return orders, nil
}

func (o *oderRepository) CreateReview(review *model.UserReview) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return o.db.WithContext(ctx).Create(review).Error
}

func (o *oderRepository) GetReviewByID(reviewID string) (*model.UserReview, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var review *model.UserReview = nil
	err := o.db.WithContext(ctx).First(&review, reviewID).Error
	return review, err
}

func (o *oderRepository) DeleteReview(review *model.UserReview) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return o.db.WithContext(ctx).Delete(review).Error
}
