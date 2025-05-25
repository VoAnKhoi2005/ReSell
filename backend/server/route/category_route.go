package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
	"github.com/VoAnKhoi2005/ReSell/middleware"
	"github.com/VoAnKhoi2005/ReSell/repository"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func RegisterCategoryRoutes(rg *gin.RouterGroup, db *gorm.DB) {
	categories := rg.Group("/categories")

	categoryRepo := repository.NewCategoryRepository(db)
	categoryService := service.NewCategoryService(categoryRepo)
	categoryController := controller.NewCategoryController(categoryService)

	//Both admin and user can access
	categories.Use(middleware.AuthMiddleware())
	categories.GET("/", categoryController.GetAllCategories)
	categories.GET("/:id", categoryController.GetCategoryByID)

	//Only admin
	admin := categories.Group("/admin")
	admin.Use(middleware.AdminAuthMiddleware())
	admin.POST("/", categoryController.CreateCategory)
	admin.PUT("/:id", categoryController.UpdateCategory)
	admin.DELETE("/:id", categoryController.DeleteCategory)
}
