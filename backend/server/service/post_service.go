package service

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/transaction"
	"github.com/google/uuid"
	"gorm.io/gorm"
	"time"
)

type PostService interface {
	// CRUD cơ bản
	GetAllPosts() ([]*model.Post, error)
	GetPostByID(id string) (*model.Post, error)
	CreatePost(req *transaction.CreatePostRequest, userID string) (*model.Post, error)
	UpdatePost(id string, req *transaction.UpdatePostRequest) (*model.Post, error)
	DeletePost(id string) error

	// Admin duyệt bài
	ApprovePost(id string) (*model.Post, error)
	RejectPost(id string) (*model.Post, error)
	HidePost(id string) (*model.Post, error)

	// Hệ thống đánh dấu đã bán hoặc hoàn lại
	MarkPostAsSold(id string) (*model.Post, error)
	RevertSoldStatus(id string) (*model.Post, error)

	// Đánh dấu đã xóa (soft delete) hoặc hoàn tác xóa
	MarkPostAsDeleted(id string) (*model.Post, error)
	RestoreDeletedPost(id string) (*model.Post, error)
}

type postService struct {
	repo repository.PostRepository
}

func (s *postService) updatePostStatus(id string, status model.PostStatus) (*model.Post, error) {
	post, err := s.GetPostByID(id)

	if err != nil {
		return nil, err
	}

	post.Status = status

	err = s.repo.Update(post)
	return post, err
}

func (s *postService) ApprovePost(id string) (*model.Post, error) {
	return s.updatePostStatus(id, model.PostStatusApproved)
}

func (s *postService) RejectPost(id string) (*model.Post, error) {
	return s.updatePostStatus(id, model.PostStatusRejected)
}

func (s *postService) HidePost(id string) (*model.Post, error) {
	return s.updatePostStatus(id, model.PostStatusHidden)
}

func (s *postService) MarkPostAsSold(id string) (*model.Post, error) {
	post, err := s.repo.GetByID(id)
	if err != nil {
		return nil, err
	}

	post.Status = model.PostStatusSold
	now := time.Now()
	post.SoldAt = &now

	err = s.repo.Update(post)
	return post, err
}

func (s *postService) RevertSoldStatus(id string) (*model.Post, error) {
	post, err := s.repo.GetByID(id)
	if err != nil {
		return nil, err
	}

	post.Status = model.PostStatusSold
	post.SoldAt = nil

	err = s.repo.Update(post)
	return post, err
}

func (s *postService) MarkPostAsDeleted(id string) (*model.Post, error) {
	post, err := s.repo.GetByID(id)
	if err != nil {
		return nil, err
	}

	post.Status = model.PostStatusDeleted
	err = s.repo.SoftDelete(post)

	return post, err
}

func (s *postService) RestoreDeletedPost(id string) (*model.Post, error) {
	// Lấy cả bài đã bị soft delete
	post, err := s.repo.GetDeletedByID(id)
	if err != nil {
		return nil, err
	}

	// Xóa trường DeletedAt để khôi phục
	post.DeletedAt = gorm.DeletedAt{}

	// Gán lại trạng thái tùy ý (ở đây cho về pending)
	post.Status = model.PostStatusPending

	// Cập nhật lại
	err = s.repo.Update(post)
	return post, err
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
		Status:      model.PostStatusPending,
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
