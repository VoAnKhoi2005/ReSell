package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type PaymentMethodRepository interface {
	GetAll() ([]*model.PaymentMethod, error)
	Create(*model.PaymentMethod) error
	Update(*model.PaymentMethod) error
	Delete(*model.PaymentMethod) error

	GetById(id string) (*model.PaymentMethod, error)
}

type paymentMethodRepository struct {
	*BaseRepository[model.PaymentMethod]
}

func NewPaymentMethodRepository(db *gorm.DB) PaymentMethodRepository {
	return &paymentMethodRepository{
		BaseRepository: NewBaseRepository[model.PaymentMethod](db),
	}
}

func (r *paymentMethodRepository) GetById(id string) (*model.PaymentMethod, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var paymentMethod model.PaymentMethod
	err := r.db.WithContext(ctx).First(&paymentMethod, "id = ?", id).Error
	return &paymentMethod, err
}
