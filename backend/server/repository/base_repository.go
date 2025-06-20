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

func (r *BaseRepository[T]) GetPaginated(page, limit int) ([]*T, int64, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var results []*T
	var count int64

	db := r.db.WithContext(ctx)
	if err := db.Count(&count).Error; err != nil {
		return nil, 0, err
	}

	err := db.Limit(limit).Offset((page - 1) * limit).Find(&results).Error

	return results, count, err

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
