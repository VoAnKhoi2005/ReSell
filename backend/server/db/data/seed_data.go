package data

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"log"
	"math/rand"
	"time"
)

func ptr[T any](v T) *T {
	return &v
}

func deleteSeedData() {
	config.DB.Exec(`
		TRUNCATE TABLE
			user_reviews,
			user_subscriptions,
			subscription_plans,
			transactions,
			messages,
			conversations,
			report_posts,
			report_users,
			notifications,
			favorite_posts,
			post_images,
			shop_orders,
			posts,
			categories,
			addresses,
			wards,
			districts,
			provinces,
			community_participants,
			communities,
			follows,
			users,
			admins,
			payment_methods
		CASCADE;
	`)

}

func GenerateSeedData() {
	deleteSeedData()
	userIDs := seedUser()
	categoryIDs := seedCategory()
	provinceIDs := seedProvince()
	districtIDs := seedDistrict(provinceIDs)
	wardsIDs := seedWard(districtIDs)
	addressIDs := seedAddress(userIDs, wardsIDs)
	postIDs := seedPost(userIDs, categoryIDs, wardsIDs)
	paymentMethodIDs := seedPaymentMethod()
	orderIDs := seedOrder(userIDs, addressIDs, postIDs, paymentMethodIDs)
	seedReview(orderIDs)
	seedReportPost(userIDs, postIDs)
	seedReportUser(userIDs)
	seedFavoritePost(userIDs, postIDs)
	seedFollow(userIDs)
	seedPostImage(postIDs)
	seedNotification(userIDs)

	log.Println("Generated seed data successfully!")
}

func randomStringIn(arr []string) string {
	return arr[rand.Intn(len(arr))]
}

func shuffleStrings(arr []string) []string {
	out := make([]string, len(arr))
	copy(out, arr)
	rand.Shuffle(len(out), func(i, j int) {
		out[i], out[j] = out[j], out[i]
	})
	return out
}

func randomStatus[T any](statusList []T) T {
	return statusList[rand.Intn(len(statusList))]
}

func randomTimeBetween(from, to time.Time) time.Time {
	if from.After(to) {
		from, to = to, from // đảo lại nếu from > to
	}
	diff := to.Sub(from)
	// random giây trong khoảng cách đó
	seconds := rand.Int63n(int64(diff.Seconds()))
	return from.Add(time.Duration(seconds) * time.Second)
}
