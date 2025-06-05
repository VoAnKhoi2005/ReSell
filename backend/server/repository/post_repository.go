package repository

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/util"
	"gorm.io/gorm"
)

type PostRepository interface {
	//Imherit from base repo
	GetAll() ([]*model.Post, error)
	Create(post *model.Post) error
	Update(post *model.Post) error
	Delete(post *model.Post) error

	//self func
	GetPostIDsByFilter(filters map[string]string, page, limit int) ([]string, int64, error)
	SoftDelete(post *model.Post) error
	GetByID(id string) (*model.Post, error)
	GetDeletedByID(id string) (*model.Post, error)
	GetAllDeleted() ([]*model.Post, error)
	Search(query string) ([]*model.Post, error)
	CreatePostImage(postImage *model.PostImage) error
	DeletePostImage(postImage *model.PostImage) error
	GetPostImage(postID, url string) (*model.PostImage, error)
	GetByFilter(filters map[string]string) ([]*model.Post, error) // Not implemented yet
}

type postRepository struct {
	*BaseRepository[model.Post] // embed
}

func NewPostRepository(db *gorm.DB) PostRepository {
	return &postRepository{
		BaseRepository: NewBaseRepository[model.Post](db),
	}
}

func (r *postRepository) GetPostIDsByFilter(filters map[string]string, page, limit int) ([]string, int64, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var postIDs []string
	var total int64

	query := r.db.WithContext(ctx).
		Model(&model.Post{}).
		Joins("JOIN addresses ON addresses.id = posts.address_id").
		Joins("JOIN wards ON wards.id = addresses.ward_id").
		Joins("JOIN districts ON districts.id = wards.district_id").
		Joins("JOIN provinces ON provinces.id = districts.province_id")

	// ========== FILTERS ==========
	if status, ok := filters["status"]; ok {
		query = query.Where("posts.status = ?", status)
	}
	if minPrice, ok := filters["min_price"]; ok {
		query = query.Where("posts.price >= ?", minPrice)
	}
	if maxPrice, ok := filters["max_price"]; ok {
		query = query.Where("posts.price <= ?", maxPrice)
	}
	if provinceID, ok := filters["province_id"]; ok {
		query = query.Where("provinces.id = ?", provinceID)
	}
	if districtID, ok := filters["district_id"]; ok {
		query = query.Where("districts.id = ?", districtID)
	}
	if wardID, ok := filters["ward_id"]; ok {
		query = query.Where("wards.id = ?", wardID)
	}
	if categoryID, ok := filters["category_id"]; ok {
		query = query.Where("posts.category_id = ?", categoryID)
	}
	if userID, ok := filters["user_id"]; ok {
		query = query.Where("posts.user_id = ?", userID)
	}

	// ========== COUNT ==========
	countQuery := query.Session(&gorm.Session{})

	if err := countQuery.Count(&total).Error; err != nil {
		return nil, 0, err
	}

	// ========== PAGINATION ==========
	err := query.
		Select("posts.id").
		Offset((page-1)*limit).
		Limit(limit).
		Order("posts.created_at DESC").
		Pluck("posts.id", &postIDs).Error
	if err != nil {
		return nil, 0, err
	}

	return postIDs, total, nil
}

func (r *postRepository) GetByID(id string) (*model.Post, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var post model.Post
	err := r.db.WithContext(ctx).Preload("User").
		Preload("Category").
		Preload("Address.Ward.District.Province").
		Preload("PostImages", func(db *gorm.DB) *gorm.DB {
			return db.Order("image_order ASC")
		}).
		First(&post, "id = ?", id).Error
	return &post, err
}

func (r *postRepository) SoftDelete(post *model.Post) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Delete(post).Error
}

func (r *postRepository) GetDeletedByID(id string) (*model.Post, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var post model.Post
	err := r.db.WithContext(ctx).Unscoped().Where("id = ? AND deleted_at IS NOT NULL", id).First(&post).Error
	return &post, err
}

func (r *postRepository) GetAllDeleted() ([]*model.Post, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var posts []*model.Post
	err := r.db.WithContext(ctx).Unscoped().Where("deleted_at IS NOT NULL").Find(&posts).Error
	return posts, err
}

func (r *postRepository) Search(queryStr string) ([]*model.Post, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var posts []*model.Post

	err := r.db.WithContext(ctx).
		Model(&model.Post{}).
		Where("posts.title ILIKE ? OR posts.description ILIKE ?", "%"+queryStr+"%", "%"+queryStr+"%").
		Preload("Address.Ward.District.Province").
		Find(&posts).Error

	return posts, err
}

func (r *postRepository) CreatePostImage(postImage *model.PostImage) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Create(postImage).Error
}

func (r *postRepository) DeletePostImage(postImage *model.PostImage) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return r.db.WithContext(ctx).Unscoped().Delete(postImage).Error
}

func (r *postRepository) GetPostImage(postID, url string) (*model.PostImage, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var postImage model.PostImage
	err := r.db.WithContext(ctx).Where("post_id = ? AND image_url = ?", postID, url).First(&postImage).Error
	return &postImage, err
}

func (r *postRepository) GetByFilter(filters map[string]string) ([]*model.Post, error) {
	panic("not implemented yet")
}
