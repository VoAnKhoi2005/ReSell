package data

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/drhodes/golorem"
	"github.com/google/uuid"
	"math/rand"
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
func seedPost(userIDs, categoryIDs, addressIDs []string) []string {
	var posts []model.Post
	var postIDs []string

	postStatusList := []model.PostStatus{
		model.PostStatusApproved,
		model.PostStatusPending,
		model.PostStatusApproved,
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
				AddressID:   &addressIDs[i], //thu tu cua address trung voi thu tu cua user
				Title:       fmt.Sprintf("Post %d", i+1),
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
