package data

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"log"
	"math/rand"
)

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
	seedPost(userIDs, categoryIDs, addressIDs)

	log.Println("Generated seed data successfully")
}

func randomStringIn(arr []string) string {
	return arr[rand.Intn(len(arr))]
}

func randomStatus[T any](statusList []T) T {
	return statusList[rand.Intn(len(statusList))]
}
