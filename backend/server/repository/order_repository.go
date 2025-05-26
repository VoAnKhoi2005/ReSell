package repository

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"gorm.io/gorm"
)

type OrderRepository interface {
	GetAll() ([]*model.ShopOrder, error)
	Create(order *model.ShopOrder) error
	Update(order *model.ShopOrder) error
	Delete(order *model.ShopOrder) error
}

type oderRepository struct {
	*BaseRepository[model.ShopOrder]
}

func NewOrderRepository(db *gorm.DB) OrderRepository {
	return &oderRepository{BaseRepository: NewBaseRepository[model.ShopOrder](db)}
}
