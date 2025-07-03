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
	orderedRootCategories := []struct {
		Name  string
		Child []string
	}{
		{"Điện tử", []string{"Điện thoại", "Laptop"}},
		{"Xe cộ", []string{"Xe máy", "Ô tô"}},
		{"Đồ gia dụng", []string{"Tủ lạnh", "Máy giặt"}},
		{"Thời trang", []string{"Quần áo", "Giày dép"}},
		{"Thể thao", []string{"Dụng cụ tập", "Trang phục thể thao"}},
	}

	//Anh phai dung voi thu tu tao category
	imageURLs := []string{
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ6biqtEPPUAb4YnddF1yJ5XaYD0gmuFHbDEw&s",                                      // gốc 1
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRscYFUouPlabc1TIeDynkNDSKWN0WN4CFH4Q&s",                                      // con 1
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTITtKKVldELuPsm7et-IVQ3eJYSkflPSNT1w&s",                                      // con 2
		"https://auto365.vn/uploads/images/tin%20t%E1%BB%A9c/2024/T6/sieu-xe-dien-ferrari-dau-tien/sieu-xe-dien-ferrari-dau-tien-2.jpg",     // gốc 2
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7dKH2W0OhupHdfqBNJwWEacWAOaxj-aBJlA&s",                                      // con 3
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_Q4mOcF_hEIgBfVKOMQgbmIMBj1upb-KthA&s",                                      // con 4
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIYJdVqKwkIj_8ugbUOiL2yhFWAv7vhHjpDQ&s",                                      // gốc 3
		"https://baohanh-lg.vn/upload/product/eqe6879a-b-angl-cl-1500x1500-2-7510.png",                                                      // con 5
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwCeTjTHnvusPp9opl3k0ebvjqZ27etsF5yQ&s",                                      // con 6
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8lelCK4CbG5C-Ff1_dQaCIAibjTrT4RDHvQ&s",                                      // gốc 4
		"https://file.hstatic.net/1000197303/file/thoi-trang-ben-vung1_b1e59f3789a34696878c226d483217e7.jpg",                                // con 7
		"https://product.hstatic.net/200000410665/product/giay-the-thao-nam-xb20649fk-2_a2f0e9136e6849da95f6c6476d9dbdec.jpg",               // con 8
		"https://bizweb.dktcdn.net/100/365/443/files/cover-sportourvn.jpg?v=1711084834856",                                                  // gốc 5
		"https://thethaominhphu.com/wp-content/uploads/2021/09/b%E1%BB%99-ta-tay-%C4%91a-n%C4%83ng-%C4%91c-1-360x360.jpg",                   // con 9
		"https://bizweb.dktcdn.net/thumb/1024x1024/100/489/974/products/vn-11134207-7qukw-lf81zy2ylpxm0a-1680708197936.jpg?v=1688524161997", // con 10
	}
	imageIdx := 0
	var categories []model.Category
	var categoryIDs []string // chỉ chứa ID của category con

	for _, item := range orderedRootCategories {
		parentID := uuid.New().String()
		parent := model.Category{
			ID:       parentID,
			Name:     item.Name,
			ImageURL: &imageURLs[imageIdx],
		}
		imageIdx++
		categories = append(categories, parent)

		for _, childName := range item.Child {
			childID := uuid.New().String()
			child := model.Category{
				ID:               childID,
				Name:             childName,
				ParentCategoryID: &parentID,
				ImageURL:         &imageURLs[imageIdx],
			}
			imageIdx++
			categories = append(categories, child)
			categoryIDs = append(categoryIDs, childID)
		}
	}

	config.DB.Create(&categories)
	return categoryIDs
}

// Voi moi user, tao so luong post ngau nhien thuoc category ngau nhien, address thi phai thuoc user do
func seedPost(userIDs, categoryIDs, wardsIDs []string) []string {
	rng := rand.New(rand.NewSource(time.Now().UnixNano()))
	start := time.Now().AddDate(0, -3, 0) // 3 tháng trước
	end := time.Now()                     // hiện tại

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
				CreatedAt:   randomTimeBetween(start, end),
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

func seedPostImage(postIDs []string) {
	var postImages []model.PostImage

	imageURLs := []string{
		"https://hoanghamobile.com/tin-tuc/wp-content/uploads/2024/10/tai-anh-phong-canh-dep-thump.jpg",
		"https://hoanghamobile.com/tin-tuc/wp-content/uploads/2024/10/tai-anh-phong-canh-dep-48.jpg",
		"https://images2.thanhnien.vn/528068263637045248/2024/1/25/e093e9cfc9027d6a142358d24d2ee350-65a11ac2af785880-17061562929701875684912.jpg",
		"https://img.tripi.vn/cdn-cgi/image/width=700,height=700/https://gcs.tripi.vn/public-tripi/tripi-feed/img/474111PXL/anh-dep-nhat-the-gioi-ve-thien-nhien_041753462.jpg",
		"https://www.vietnamworks.com/hrinsider/wp-content/uploads/2023/12/hinh-nen-thien-nhien-3d-001.jpg",
		"https://marketplace.canva.com/MADTMzeJyPs/1/thumbnail_large-1/canva-extraordinary-nature-MADTMzeJyPs.jpg",
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRlSOdeBhdoQSYzRFK8eZkXSYUoMIHzXT-R8A&s",
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRc8gvzRMNe9I-ssBiQ7d-pcycyJYqNJnroNg&s",
		"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTqkUIFyavTun-7M3fGKIEB5CZ3mqRbxtMssg&s",
		"https://images2.thanhnien.vn/528068263637045248/2024/1/25/4f250101ab159dc6eceba37546eb360e-65a11acf74b28880-17061562928811862916773.jpg",
	}

	for _, postID := range postIDs {
		shuffleImageURLs := shuffleStrings(imageURLs)
		for j := 0; j < 3; j++ {
			postImage := model.PostImage{
				PostID:     &postID,
				ImageURL:   shuffleImageURLs[j],
				ImageOrder: uint(j + 1),
			}
			postImages = append(postImages, postImage)
		}
	}
	config.DB.Create(&postImages)
}
