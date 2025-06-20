package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type TransactionRepository interface {
	//Inherit
	GetPaginated(page, limit int) ([]*model.Transaction, int64, error)
	Create(tx *model.Transaction) error

	UpdateStatusByIntentID(intentID string, status model.TransactionStatus) error
	UpdateStatusAndErrorByIntentID(intentID, status, message string) error
}

type transactionRepository struct {
	*BaseRepository[model.Transaction]
}

func NewTransactionRepository(db *gorm.DB) TransactionRepository {
	return &transactionRepository{
		NewBaseRepository[model.Transaction](db),
	}
}

func (r *transactionRepository) UpdateStatusByIntentID(intentID string, status model.TransactionStatus) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.
		WithContext(ctx).
		Model(&model.Transaction{}).
		Where("stripe_payment_intent_id = ?", intentID).
		Update("status", status).Error
}

func (r *transactionRepository) UpdateStatusAndErrorByIntentID(intentID, status, message string) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.Model(&model.Transaction{}).
		WithContext(ctx).
		Where("stripe_payment_intent_id = ?", intentID).
		Updates(map[string]interface{}{
			"status":        status,
			"error_message": message,
		}).Error
}
