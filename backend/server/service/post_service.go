package service

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/google/uuid"
	"gorm.io/gorm"
	"time"
)

type PostService interface {
	// CRUD cơ bản
	GetAdminPosts(filters map[string]string, page, limit int) ([]*dto.PostListAdminDTO, int64, error)
	GetUserPosts(ownerID string, filters map[string]string, page, limit int) ([]*dto.PostListUserDTO, int64, error)
	GetPostByID(id string) (*model.Post, error)
	CreatePost(req *transaction.CreatePostRequest, userID string) (*model.Post, error)
	UpdatePost(id string, req *transaction.UpdatePostRequest) (*model.Post, error)
	DeletePost(id string) error
	GetAllDeletedPosts() ([]*model.Post, error)
	GetDeletedPostByID(id string) (*model.Post, error)
	//SearchPosts(query string) ([]*dto.PostListUserDTO, error)
	CreatePostImage(postID, url string) (*model.PostImage, error)
	DeletePostImage(postID, url string) error

	// Admin duyệt bài
	ApprovePost(id string) (*model.Post, error)
	RejectPost(id string) (*model.Post, error)
	//HidePost(id string) (*model.Post, error)
	//UnhidePost(id string) (*model.Post, error)

	// Hệ thống đánh dấu đã bán hoặc hoàn lại
	MarkPostAsSold(id string) (*model.Post, error)
	RevertSoldStatus(id string) (*model.Post, error)

	// Đánh dấu đã xóa (soft delete) hoặc hoàn tác xóa
	MarkPostAsDeleted(id string) (*model.Post, error)
	RestoreDeletedPost(id string) (*model.Post, error)

	GetFollowedPosts(userID string, filters map[string]string, page, limit int) ([]*dto.PostListUserDTO, int64, error)
	GetOwnPosts(userID string, filters map[string]string, page, limit int) ([]*dto.PostListUserDTO, int64, error)
}

type postService struct {
	repo repository.PostRepository
}

func (s *postService) GetAdminPosts(filters map[string]string, page, limit int) ([]*dto.PostListAdminDTO, int64, error) {
	return s.repo.GetAdminPostsByFilter(filters, page, limit)
}

func (s *postService) GetUserPosts(ownerID string, filters map[string]string, page, limit int) ([]*dto.PostListUserDTO, int64, error) {
	return s.repo.GetUserPostsByFilter(ownerID, filters, page, limit)
}

func (s *postService) GetFollowedPosts(userID string, filters map[string]string, page, limit int) ([]*dto.PostListUserDTO, int64, error) {
	return s.repo.GetFollowedPosts(userID, filters, page, limit)
}

func (s *postService) GetOwnPosts(userID string, filters map[string]string, page, limit int) ([]*dto.PostListUserDTO, int64, error) {
	return s.repo.GetOwnPosts(userID, filters, page, limit)
}

func (s *postService) updatePostStatus(id string, status model.PostStatus) (*model.Post, error) {
	post, err := s.GetPostByID(id)

	if err != nil {
		return nil, err
	}

	post.Status = status

	err = s.repo.Update(post)

	cacheKey := "post:" + id
	ctx, cancel := util.NewRedisContext()
	defer cancel()

	_ = config.RedisClient.Del(ctx, cacheKey)
	return post, err
}

func (s *postService) ApprovePost(id string) (*model.Post, error) {
	return s.updatePostStatus(id, model.PostStatusApproved)
}

func (s *postService) RejectPost(id string) (*model.Post, error) {
	return s.updatePostStatus(id, model.PostStatusRejected)
}

//func (s *postService) HidePost(id string) (*model.Post, error) {
//	return s.updatePostStatus(id, model.PostStatusHidden)
//}
//
//func (s *postService) UnhidePost(id string) (*model.Post, error) {
//	post, err := s.repo.GetByID(id)
//	if err != nil {
//		return nil, err
//	}
//	if post.Status != model.PostStatusHidden {
//		return nil, gorm.ErrRecordNotFound
//	}
//	return s.updatePostStatus(id, model.PostStatusApproved)
//}

func (s *postService) MarkPostAsSold(id string) (*model.Post, error) {
	post, err := s.repo.GetByID(id)
	if err != nil {
		return nil, err
	}

	post.Status = model.PostStatusSold
	now := time.Now().UTC()
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
		WardID:      &req.WardID,
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

	isChange := false

	if req.CategoryID != nil && *req.CategoryID != "" {
		post.CategoryID = req.CategoryID
		isChange = true
	}

	if req.WardID != nil && *req.WardID != "" {
		post.WardID = req.WardID
		isChange = true
	}

	if req.Title != nil && *req.Title != "" {
		post.Title = *req.Title
		isChange = true
	}

	if req.Description != nil && *req.Description != "" {
		post.Description = *req.Description
		isChange = true
	}

	if req.Price != nil {
		post.Price = *req.Price
		isChange = true
	}

	if !isChange {
		return nil, errors.New("no change")
	}

	//post.CategoryID = &req.CategoryID
	//post.AddressID = &req.AddressID
	//post.Title = req.Title
	//post.Description = req.Description
	//post.Price = req.Price

	err = s.repo.Update(post)
	return post, err
}

func (s *postService) GetPostByID(id string) (*model.Post, error) {
	// 1. Kiểm tra cache Redis trước
	cacheKey := "post:" + id
	ctx, cancel := util.NewRedisContext()
	defer cancel()

	val, err := config.RedisClient.Get(ctx, cacheKey).Result()
	if err == nil {
		var post model.Post
		if err := json.Unmarshal([]byte(val), &post); err == nil {
			fmt.Println("Cache hit")
			return &post, nil // HIT cache
		}
	}

	// 2. Nếu cache miss → fallback DB
	post, err := s.repo.GetByID(id)
	if err != nil {
		return nil, err
	}

	// 3. Set lại cache cho lần sau
	data, _ := json.Marshal(post)
	config.RedisClient.Set(ctx, cacheKey, data, 5*time.Minute)

	fmt.Println("Cache miss")
	return post, nil
}

func (s *postService) DeletePost(id string) error {
	post, err := s.repo.GetDeletedByID(id)
	if err != nil {
		return err
	}
	return s.repo.Delete(post)
}

func (s *postService) GetAllDeletedPosts() ([]*model.Post, error) {
	return s.repo.GetAllDeleted()
}

func (s *postService) GetDeletedPostByID(id string) (*model.Post, error) {
	return s.repo.GetDeletedByID(id)
}

func NewPostService(repo repository.PostRepository) PostService {
	return &postService{repo: repo}
}

//func (s *postService) SearchPosts(query string) ([]*dto.PostListUserDTO, error) {
//	return s.repo.Search(query)
//}

func (s *postService) CreatePostImage(postID, url string) (*model.PostImage, error) {
	var maxOrder uint
	err := config.DB.Model(&model.PostImage{}).
		Where("post_id = ?", postID).
		Select("COALESCE(MAX(image_order), 0)").Scan(&maxOrder).Error

	if err != nil {
		return nil, fmt.Errorf("failed to get max image order: %w", err)
	}

	postImage := &model.PostImage{
		PostID:     &postID,
		ImageURL:   url,
		ImageOrder: maxOrder + 1,
	}

	err = s.repo.CreatePostImage(postImage)
	return postImage, err
}

func (s *postService) DeletePostImage(postID, url string) error {
	postImage, err := s.repo.GetPostImage(postID, url)
	if err != nil {
		return err
	}
	return s.repo.DeletePostImage(postImage)
}
