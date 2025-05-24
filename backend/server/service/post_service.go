package service

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/google/uuid"
)

type PostService interface {
	GetAllPosts() ([]*model.Post, error)
	GetPostByID(id string) (*model.Post, error)
	CreatePost(req *transaction.CreatePostRequest, userID string) (*model.Post, error)
	UpdatePost(id string, req *transaction.UpdatePostRequest) (*model.Post, error)
	DeletePost(id string) error
}

type postService struct {
	repo repository.PostRepository
}

func (s *postService) CreatePost(req *transaction.CreatePostRequest, userID string) (*model.Post, error) {
	post := &model.Post{
		ID:          uuid.New().String(),
		UserID:      &userID,
		CategoryID:  &req.CategoryID,
		AddressID:   &req.AddressID,
		Title:       req.Title,
		Description: req.Description,
		Price:       req.Price,
	}
	err := s.repo.Create(post)

	return post, err
}

func (s *postService) UpdatePost(id string, req *transaction.UpdatePostRequest) (*model.Post, error) {
	post, err := s.GetPostByID(id)

	if err != nil {
		return nil, err
	}

	post.CategoryID = &req.CategoryID
	post.AddressID = &req.AddressID
	post.Title = req.Title
	post.Description = req.Description
	post.Price = req.Price

	err = s.repo.Update(post)
	return post, err
}

func (s *postService) GetAllPosts() ([]*model.Post, error) {
	return s.repo.GetAll()
}

func (s *postService) GetPostByID(id string) (*model.Post, error) {
	return s.repo.GetByID(id)
}

func (s *postService) DeletePost(id string) error {
	post, err := s.repo.GetByID(id)
	if err != nil {
		return err
	}
	return s.repo.Delete(post)
}

func NewPostService(repo repository.PostRepository) PostService {
	return &postService{repo: repo}
}
