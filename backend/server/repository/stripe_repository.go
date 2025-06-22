package repository

import "gorm.io/gorm"

type StripeRepository interface {
}

type stripeRepository struct {
	db *gorm.DB
}

func NewStripeRepository(db *gorm.DB) StripeRepository {
	return &stripeRepository{
		db: db,
	}
}
