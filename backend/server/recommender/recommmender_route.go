package recommender

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterRecommenderRoute(rg *gin.RouterGroup, db *gorm.DB) {
	repo := NewRecommenderRepository(db)
	service := NewRecommenderService(repo)
	controller := NewRecommenderController(service)

	recommenderRoute := rg.Group("/recommender")
	recommenderRoute.Use(middleware.DockerInternalAuth())

	recommenderRoute.GET("/profile/:user_id", controller.GetBuyerProfile)
	recommenderRoute.GET("/posts-features", controller.GetPostsFeatures)
	recommenderRoute.GET("/posts-candidate-id", controller.GetCandidatePostsID)
}
