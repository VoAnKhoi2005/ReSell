package repository

import (
	"gorm.io/gorm"
)

type GlobalRepository struct {
	userRepo UserRepository
}

func (g *GlobalRepository) IncreaseReputation(userId string, amount int) error {
	return g.userRepo.IncreaseReputation(userId, amount)
}

func (g *GlobalRepository) DecreaseReputation(userId string, amount int) error {
	return g.userRepo.DecreaseReputation(userId, amount)
}

func NewGlobalRepository(db *gorm.DB) *GlobalRepository {
	return &GlobalRepository{
		userRepo: NewUserRepository(db),
	}
}

var GlobalRepo *GlobalRepository

func InitGlobalRepository(db *gorm.DB) {
	GlobalRepo = NewGlobalRepository(db)
}
