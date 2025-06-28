package recommender

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
)

type Service interface {
	GetBuyerProfile(userID string) (*dto.BuyerProfile, error)
	GetPostsFeatures(postIDs []string, userID string) ([]*dto.PostFeatures, error)
	GetCandidatePostsID(page int, pageSize int) ([]string, error)
}

type recommenderService struct {
	repo Repository
}

func NewRecommenderService(repo Repository) Service {
	return &recommenderService{repo}
}

func (r *recommenderService) GetBuyerProfile(userID string) (*dto.BuyerProfile, error) {
	return r.repo.GetBuyerProfile(userID)
}

func (r *recommenderService) GetPostsFeatures(postIDs []string, userID string) ([]*dto.PostFeatures, error) {
	profile, err := r.GetBuyerProfile(userID)
	if err != nil {
		return nil, err
	}

	var features []*dto.PostFeatures
	for _, postID := range postIDs {
		feature, err := r.repo.GetPostFeatures(postID, profile)
		if err != nil {
			return nil, err
		}

		features = append(features, feature)
	}

	return features, nil
}

func (r *recommenderService) GetCandidatePostsID(page int, pageSize int) ([]string, error) {
	return r.repo.GetCandidatePostsID(page, pageSize)
}
