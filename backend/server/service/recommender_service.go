package service

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"sort"
)

type RecommenderService interface {
	GetBuyerProfile(userID string) (*dto.BuyerProfile, error)
	GetPostsFeatures(postIDs []string, userID string) ([]*dto.PostFeatures, error)
	GetCandidatePostsID(page int, pageSize int) ([]string, int64, error)

	GetRecommendation(userID string, page int, pageSize int) ([]*dto.PostListUserDTO, int64, error)
}

type recommenderService struct {
	repo     repository.RecommenderRepository
	postRepo repository.PostRepository
}

func NewRecommenderService(repo repository.RecommenderRepository, postRepo repository.PostRepository) RecommenderService {

	return &recommenderService{
		repo,
		postRepo,
	}
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

func (r *recommenderService) GetCandidatePostsID(page int, pageSize int) ([]string, int64, error) {
	return r.repo.GetCandidatePostsID(page, pageSize)
}

func (r *recommenderService) GetRecommendation(userID string, page int, pageSize int) ([]*dto.PostListUserDTO, int64, error) {
	candidatePostsID, total, err := r.repo.GetCandidatePostsID(page, pageSize)
	if err != nil {
		return nil, 0, err
	}

	postsFeatures, err := r.GetPostsFeatures(candidatePostsID, userID)
	if err != nil {
		return nil, 0, err
	}

	sort.Slice(postsFeatures, func(i, j int) bool {
		return postsFeatures[i].FinalScore > postsFeatures[j].FinalScore
	})

	postIDs := make([]string, len(postsFeatures))
	for i, pf := range postsFeatures {
		postIDs[i] = pf.PostID
	}

	postsDTO, _, err := r.postRepo.GetPostsByIdList(userID, postIDs, 1, 100000000)

	return postsDTO, total, nil
}
