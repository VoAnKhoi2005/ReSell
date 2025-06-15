package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type ReviewRepository interface {
	CreateReview(review *model.UserReview) error
	GetReviewByOrderID(orderID string) (*model.UserReview, error)
	DeleteReview(review *model.UserReview) error
}

type reviewRepository struct {
	db *gorm.DB
}

func NewReviewRepository(db *gorm.DB) ReviewRepository {
	return &reviewRepository{db: db}
}

func (r *reviewRepository) CreateReview(review *model.UserReview) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Create(review).Error
}

func (r *reviewRepository) GetReviewByOrderID(orderID string) (*model.UserReview, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var review *model.UserReview = nil
	err := r.db.WithContext(ctx).First(&review, "order_id = ?", orderID).Error
	return review, err
}

func (r *reviewRepository) DeleteReview(review *model.UserReview) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Delete(review).Error
}
