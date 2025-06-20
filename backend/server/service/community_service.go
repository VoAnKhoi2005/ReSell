package service

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/google/uuid"
)

type CommunityService interface {
	GetCommunities(page, limit int) ([]*model.Community, int64, error)
	GetCommunityByID(id string) (*model.Community, error)
	CreateCommunity(req *transaction.CreateCommunityRequest) (*model.Community, error)
	UpdateCommunity(id string, req *transaction.UpdateCommunityRequest) (*model.Community, error)
	DeleteCommunity(id string) error
	UnbanCommunity(id string) (*model.Community, error)
	BanCommunity(id string) (*model.Community, error)
}

type communityService struct {
	repo repository.CommunityRepository
}

func NewCommunityService(repo repository.CommunityRepository) CommunityService {
	return &communityService{repo: repo}
}

func (s *communityService) GetCommunities(page, limit int) ([]*model.Community, int64, error) {
	return s.repo.GetPaginated(page, limit)
}

func (s *communityService) GetCommunityByID(id string) (*model.Community, error) {
	return s.repo.GetByID(id)
}

func (s *communityService) CreateCommunity(req *transaction.CreateCommunityRequest) (*model.Community, error) {
	community := &model.Community{
		ID:          uuid.New().String(),
		Name:        req.Name,
		Description: req.Description,
		Type:        req.Type,
		Status:      model.CommunityStatusActive,
	}
	err := s.repo.Create(community)
	return community, err
}

func (s *communityService) UpdateCommunity(id string, req *transaction.UpdateCommunityRequest) (*model.Community, error) {
	community, err := s.repo.GetByID(id)
	if err != nil {
		return nil, err
	}
	community.Name = req.Name
	community.Description = req.Description
	community.Type = req.Type
	err = s.repo.Update(community)
	return community, err
}

func (s *communityService) DeleteCommunity(id string) error {
	community, err := s.repo.GetByID(id)
	if err != nil {
		return err
	}
	return s.repo.Delete(community)
}

func (s *communityService) updateCommunityStatus(id string, status model.CommunityStatus) (*model.Community, error) {
	community, err := s.GetCommunityByID(id)

	if err != nil {
		return nil, err
	}

	community.Status = status

	err = s.repo.Update(community)
	return community, err
}

func (s *communityService) UnbanCommunity(id string) (*model.Community, error) {
	return s.updateCommunityStatus(id, model.CommunityStatusActive)
}

func (s *communityService) BanCommunity(id string) (*model.Community, error) {
	return s.updateCommunityStatus(id, model.CommunityStatusBanned)
}
