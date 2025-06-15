package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type BaseRepository[T any] struct {
	db *gorm.DB
}

func NewBaseRepository[T any](db *gorm.DB) *BaseRepository[T] {
	return &BaseRepository[T]{db: db}
}

func (r *BaseRepository[T]) GetAll() ([]*T, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var results []*T
	err := r.db.WithContext(ctx).Find(&results).Error
	return results, err
}

func (r *BaseRepository[T]) Create(data *T) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Create(data).Error
}

func (r *BaseRepository[T]) Update(data *T) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Save(data).Error
}

func (r *BaseRepository[T]) Delete(data *T) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Unscoped().Delete(data).Error
}
