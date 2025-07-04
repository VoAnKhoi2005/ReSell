package data

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	lorem "github.com/drhodes/golorem"
	"github.com/google/uuid"
	"gorm.io/gorm"
	"math/rand"
	"time"
)

// Voi moi user, tao random order cua random post (cac post khong dc lap lai) voi random payment method
// User ko dc tao order voi post cua chinh minh
// Khong phai tat ca user va post deu co order
func seedOrder(userIDs, addressIDs, postIDs, paymentMethodIDs []string) []string {
	var orders []model.ShopOrder
	var orderIDs []string

	var orderStatusList = []model.OrderStatus{
		model.OrderStatusCancelled,
		model.OrderStatusPending,
		model.OrderStatusProcessing,
		model.OrderStatusShipping,
		model.OrderStatusCompleted,
	}

	shuffledPostIDs := shuffleStrings(postIDs)
	k := 0

	for i, userID := range userIDs {
		orderCounts := rand.Intn(4)

		for j := 1; j <= orderCounts; j++ {
			for {
				if k >= len(shuffledPostIDs) {
					config.DB.Create(&orders)
					return orderIDs
				}

				postID := shuffledPostIDs[k]
				k++

				post := model.Post{}
				err := config.DB.
					Session(&gorm.Session{NewDB: true}).
					Model(&model.Post{}).
					Where("id = ?", postID).
					First(&post).Error

				if err != nil {
					fmt.Println("🔴 Không tìm thấy post:", postID)
					continue
				}

				if post.UserID == nil || *post.UserID == userID || post.Status != model.PostStatusSold {
					continue
				}

				id := uuid.New().String()
				paymentMethodId := randomStringIn(paymentMethodIDs)
				status := randomStatus(orderStatusList)

				o := model.ShopOrder{
					ID:              id,
					UserId:          &userID,
					PostId:          &post.ID,
					PaymentMethodId: &paymentMethodId,
					Status:          status,
					AddressId:       &addressIDs[i],
					Total:           int(post.Price),
					CreatedAt:       randomTimeBetween(post.CreatedAt, time.Now()),
				}

				orders = append(orders, o)
				orderIDs = append(orderIDs, id)
				break
			}
		}
	}

	config.DB.Create(&orders)
	return orderIDs
}

// order nao co status la sold thi se co review
func seedReview(orderIDs []string) {
	var reviews []model.UserReview

	for i, orderID := range orderIDs {
		var order model.ShopOrder
		config.DB.Where("id = ?", orderID).First(&order)

		if order.Status != model.OrderStatusCompleted {
			continue
		}

		review := model.UserReview{
			OrderId:   &orderID,
			UserId:    order.UserId,
			Rating:    i % 5,
			Comment:   lorem.Paragraph(2, 4),
			CreatedAt: randomTimeBetween(order.CreatedAt, time.Now()),
		}

		reviews = append(reviews, review)
	}
	config.DB.Create(&reviews)
}
