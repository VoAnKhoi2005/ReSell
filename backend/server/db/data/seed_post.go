package data

import (
	"errors"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/drhodes/golorem"
	"github.com/google/uuid"
	"gorm.io/gorm"
	"log"
	"math/rand"
	"time"
)

func seedCategory() []string {
	var categories []model.Category
	var categoryIDs []string
	for i := 1; i <= 10; i++ {
		id := uuid.New().String()
		c := model.Category{
			ID:   id,
			Name: fmt.Sprintf("Category %d", i),
		}

		categories = append(categories, c)
		categoryIDs = append(categoryIDs, id)
	}

	config.DB.Create(&categories)

	return categoryIDs
}

// Voi moi user, tao so luong post ngau nhien thuoc category ngau nhien, address thi phai thuoc user do
func seedPost(userIDs, categoryIDs, wardsIDs []string) []string {
	rng := rand.New(rand.NewSource(time.Now().UnixNano()))

	var posts []model.Post
	var postIDs []string

	postStatusList := []model.PostStatus{
		model.PostStatusApproved,
		model.PostStatusPending,
		model.PostStatusSold,
		model.PostStatusRejected,
	}

	for i, userID := range userIDs {

		postCount := rand.Intn(6)
		for j := 1; j <= postCount; j++ {
			id := uuid.New().String()
			categoryID := randomStringIn(categoryIDs)
			status := randomStatus(postStatusList)

			post := model.Post{
				ID:          id,
				UserID:      &userID,
				CategoryID:  &categoryID,
				WardID:      &wardsIDs[rng.Intn(len(wardsIDs))],
				Title:       fmt.Sprintf("Post %02d - %02d", i+1, j),
				Description: lorem.Paragraph(2, 4),
				Status:      status,
				Price:       uint(rand.Intn(900000) + 100000), // 100k–1tr
			}
			posts = append(posts, post)
			postIDs = append(postIDs, id)
		}
	}

	config.DB.Create(&posts)
	return postIDs
}

func seedFavoritePost(userIDs, postIDs []string) {
	var favoritePosts []model.FavoritePost

	for _, userID := range userIDs {
		postCounts := rand.Intn(4)

		for j := 1; j <= postCounts; j++ {

			//Check truong hop user thich bai cua chinh minh
			var postID string
			for {
				postID = randomStringIn(postIDs)

				var postOwnerID string
				config.DB.Where("id = ?", postID).Pluck("user_id", &postOwnerID)

				if userID == postOwnerID {
					continue
				}
				break
			}

			//Check xem user co thich bai nay chua, tranh trung
			var existingFavoritePost model.FavoritePost
			err := config.DB.Where("post_id = ? AND user_id = ?", postID, userID).First(&existingFavoritePost).Error

			if err == nil {
				continue // đã có bản ghi
			}
			if !errors.Is(err, gorm.ErrRecordNotFound) {
				log.Printf("DB error: %v", err)
				continue
			}

			favoritePost := model.FavoritePost{
				UserID: &userID,
				PostID: &postID,
			}

			favoritePosts = append(favoritePosts, favoritePost)
		}
	}
	config.DB.Create(&favoritePosts)
}
