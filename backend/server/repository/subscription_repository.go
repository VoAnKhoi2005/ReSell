package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type SubscriptionRepository interface {
	GetAll() ([]*model.SubscriptionPlan, error)
	GetByID(id string) (*model.SubscriptionPlan, error)
	Create(plan *model.SubscriptionPlan) error
	Update(plan *model.SubscriptionPlan) error
	Delete(plan *model.SubscriptionPlan) error
}

type subscriptionRepository struct {
	*BaseRepository[model.SubscriptionPlan]
}

func NewSubscriptionRepository(db *gorm.DB) SubscriptionRepository {
	return &subscriptionRepository{
		BaseRepository: NewBaseRepository[model.SubscriptionPlan](db),
	}
}

func (r *subscriptionRepository) GetByID(id string) (*model.SubscriptionPlan, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var plan model.SubscriptionPlan
	err := r.db.WithContext(ctx).First(&plan, "id = ?", id).Error
	return &plan, err
}
