package repository

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type ReportRepository interface {
	GetReportUserPaginated(page, limit int) ([]*model.ReportUser, int64, error)
	GetReportPostPaginated(page, limit int) ([]*model.ReportPost, int64, error)

	GetReportUser(reporterID, reportedID string) (*model.ReportUser, error)
	GetReportPost(reporterID, reportedID string) (*model.ReportPost, error)

	CreateReportUser(*model.ReportUser) error
	CreateReportPost(*model.ReportPost) error
}

type reportRepository struct {
	db *gorm.DB
}

func (r reportRepository) GetReportUser(reporterID, reportedID string) (*model.ReportUser, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()
	var reportUser model.ReportUser
	err := r.db.WithContext(ctx).First(&reportUser, "reporter_id = ? and reported_id", reporterID, reportedID).Error
	return &reportUser, err
}

func (r reportRepository) GetReportPost(reporterID, reportedID string) (*model.ReportPost, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()
	var reportPost model.ReportPost
	err := r.db.WithContext(ctx).First(&reportPost, "reporter_id = ? and reported_id", reporterID, reportedID).Error
	return &reportPost, err
}

func (r reportRepository) GetReportUserPaginated(page, limit int) ([]*model.ReportUser, int64, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var results []*model.ReportUser
	var count int64

	db := r.db.WithContext(ctx)
	if err := db.Model(&model.ReportUser{}).Count(&count).Error; err != nil {
		return nil, 0, err
	}

	err := db.Limit(limit).Offset((page - 1) * limit).Preload("Reported").Preload("Reporter").Order("created_at desc").Find(&results).Error

	return results, count, err

}

func (r reportRepository) GetReportPostPaginated(page, limit int) ([]*model.ReportPost, int64, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var results []*model.ReportPost
	var count int64

	db := r.db.WithContext(ctx)
	if err := db.Model(&model.ReportPost{}).Count(&count).Error; err != nil {
		return nil, 0, err
	}

	err := db.Limit(limit).Offset((page - 1) * limit).Preload("Reported").Preload("Reporter").Order("created_at desc").Find(&results).Error

	return results, count, err

}

func (r reportRepository) CreateReportUser(user *model.ReportUser) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()
	return r.db.WithContext(ctx).Create(user).Error
}

func (r reportRepository) CreateReportPost(post *model.ReportPost) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()
	return r.db.WithContext(ctx).Create(post).Error
}

func NewReportRepository(db *gorm.DB) ReportRepository {
	return &reportRepository{db: db}
}
