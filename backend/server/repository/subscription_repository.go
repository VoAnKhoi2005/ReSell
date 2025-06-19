package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"gorm.io/gorm"
)

type SubscriptionRepository interface {
	Create(subscription *model.UserSubscription) error
}

type subscriptionRepository struct {
	*BaseRepository[model.UserSubscription]
}

func NewSubscriptionRepository(db *gorm.DB) SubscriptionRepository {
	return &subscriptionRepository{
		NewBaseRepository(db),
	}
}

func (r *subscriptionRepository) GetPlanByID(id string) (*model.SubscriptionPlan, error) {
	var plan model.SubscriptionPlan
	err := r.db.First(&plan, "id = ?", id).Error
	return &plan, err
}

func (r *subscriptionRepository) GetUserByID(id string) (*model.User, error) {
	var user model.User
	err := r.db.First(&user, "id = ?", id).Error
	return &user, err
}
