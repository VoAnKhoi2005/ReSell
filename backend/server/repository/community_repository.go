package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type CommunityRepository interface {
	GetPaginated(page, limit int) ([]*model.Community, int64, error)
	GetByID(id string) (*model.Community, error)
	Create(community *model.Community) error
	Update(community *model.Community) error
	Delete(community *model.Community) error
}

type communityRepository struct {
	*BaseRepository[model.Community]
}

func NewCommunityRepository(db *gorm.DB) CommunityRepository {
	return &communityRepository{
		BaseRepository: NewBaseRepository[model.Community](db),
	}
}

func (r *communityRepository) GetByID(id string) (*model.Community, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var community model.Community
	err := r.db.WithContext(ctx).First(&community, "id = ?", id).Error
	return &community, err
}
