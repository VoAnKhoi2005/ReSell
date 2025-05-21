package route

import (
	"github.com/VoAnKhoi2005/ReSell/controller"
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

	//categories.Use(middleware.JwtAuthMiddleware())

	categories.GET("/", categoryController.GetAllCategories)
	categories.GET("/:id", categoryController.GetCategoryByID)
	categories.POST("/", categoryController.CreateCategory)
	categories.PUT("/:id", categoryController.UpdateCategory)
	categories.DELETE("/:id", categoryController.DeleteCategory)
}
