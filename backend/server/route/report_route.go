package route

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/controller"
	"github.com/VoAnKhoi2005/ReSell/backend/server/middleware"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterReportRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	reportRepo := repository.NewReportRepository(db)
	reportService := service.NewReportService(reportRepo)
	reportController := controller.NewReportController(reportService)

	report := rg.Group("/report")
	report.Use(middleware.AuthMiddleware())
	report.POST("/users", reportController.CreateReportUser)
	report.POST("/posts", reportController.CreateReportPost)

	admin := rg.Group("/admin/report")
	admin.Use(middleware.AdminAuthMiddleware())
	admin.GET("/users", reportController.GetReportUsers)
	admin.GET("/posts", reportController.GetReportPosts)
}
