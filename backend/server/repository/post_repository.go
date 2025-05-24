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
	GetByID(id string) (*model.Post, error)
}

type postRepository struct {
	*BaseRepository[model.Post] // embed
}

func NewPostRepository(db *gorm.DB) PostRepository {
	return &postRepository{
		BaseRepository: NewBaseRepository[model.Post](db),
	}
}

func (r *postRepository) GetByID(id string) (*model.Post, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var post model.Post
	err := r.db.WithContext(ctx).First(&post, "id = ?", id).Error
	return &post, err
}
