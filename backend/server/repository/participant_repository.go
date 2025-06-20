package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type ParticipantRepository interface {
	GetPaginated(page, limit int) ([]*model.CommunityParticipant, int64, error)
	Create(participant *model.CommunityParticipant) error
	Update(participant *model.CommunityParticipant) error
	Delete(participant *model.CommunityParticipant) error
	GetParticipant(userID, communityID string) (*model.CommunityParticipant, error)
}

type participantRepository struct {
	*BaseRepository[model.CommunityParticipant]
}

func NewParticipantRepository(db *gorm.DB) ParticipantRepository {
	return &participantRepository{
		BaseRepository: NewBaseRepository[model.CommunityParticipant](db),
	}
}

func (r *participantRepository) GetParticipant(userID, communityID string) (*model.CommunityParticipant, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var participant model.CommunityParticipant
	err := r.db.WithContext(ctx).First(&participant, "user_id = ? AND community_id = ?", userID, communityID).Error
	return &participant, err
}
