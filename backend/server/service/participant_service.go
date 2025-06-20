package service

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
)

type ParticipantService interface {
	GetParticipants(page int, limit int) ([]*model.CommunityParticipant, int64, error)
	ApproveParticipant(userID, communityID string) (*model.CommunityParticipant, error)
	RejectPaticipant(userID, communityID string) (*model.CommunityParticipant, error)
	LeaveCommunity(userID, communityID string) error
	JoinCommunity(userID, communityID string) (*model.CommunityParticipant, error)
	GetParticipant(userID, communityID string) (*model.CommunityParticipant, error)
}

type participantService struct {
	repo repository.ParticipantRepository
}

func (s *participantService) ApproveParticipant(userID, communityID string) (*model.CommunityParticipant, error) {
	return s.updateCommunityStatus(userID, communityID, model.ParticipantStatusApproved)
}

func (s *participantService) RejectPaticipant(userID, communityID string) (*model.CommunityParticipant, error) {
	return s.updateCommunityStatus(userID, communityID, model.ParticipantStatusRejected)
}

func (s *participantService) LeaveCommunity(userID, communityID string) error {
	participant, err := s.repo.GetParticipant(userID, communityID)
	if err != nil {
		return err
	}
	return s.repo.Delete(participant)
}

func (s *participantService) JoinCommunity(userID, communityID string) (*model.CommunityParticipant, error) {
	participant, err := s.repo.GetParticipant(userID, communityID)
	if err != nil {
		return nil, err
	}

	if participant != nil {
		switch participant.Status {
		case model.ParticipantStatusRejected:
			participant.Status = model.ParticipantStatusPending
			err := s.repo.Update(participant)
			return participant, err
		case model.ParticipantStatusPending, model.ParticipantStatusApproved:
			// Đã gửi hoặc đã duyệt rồi, không cần tạo mới
			return participant, nil
		}
	}

	// Chưa từng tham gia -> tạo mới
	participant = &model.CommunityParticipant{
		UserID:      userID,
		CommunityID: communityID,
		Status:      model.ParticipantStatusPending,
		Role:        model.ParticipantRoleParticipant,
	}

	err = s.repo.Create(participant)
	return participant, err
}

func NewParticipantService(repo repository.ParticipantRepository) ParticipantService {
	return &participantService{
		repo: repo,
	}
}

func (s *participantService) GetParticipants(page int, limit int) ([]*model.CommunityParticipant, int64, error) {
	return s.repo.GetPaginated(page, limit)
}

func (s *participantService) GetParticipant(userID, communityID string) (*model.CommunityParticipant, error) {
	return s.repo.GetParticipant(userID, communityID)
}

func (s *participantService) updateCommunityStatus(userID, communityID string, status model.ParticipantStatus) (*model.CommunityParticipant, error) {
	participant, err := s.GetParticipant(userID, communityID)

	if err != nil {
		return nil, err
	}

	participant.Status = status

	err = s.repo.Update(participant)
	return participant, err
}
