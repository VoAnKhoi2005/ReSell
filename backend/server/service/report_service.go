package service

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
)

type ReportService interface {
	GetReportUserPaginated(page, limit int) ([]*model.ReportUser, int64, error)
	GetReportPostPaginated(page, limit int) ([]*model.ReportPost, int64, error)

	GetReportUser(reporterID, reportedID string) (*model.ReportUser, error)
	GetReportPost(reporterID, reportedID string) (*model.ReportPost, error)

	CreateReportUser(req *transaction.ReportUserRequest, userID string) error
	CreateReportPost(req *transaction.ReportPostRequest, userID string) error
}

type reportService struct {
	repo repository.ReportRepository
}

func (r reportService) GetReportUser(reporterID, reportedID string) (*model.ReportUser, error) {
	return r.repo.GetReportUser(reporterID, reportedID)
}

func (r reportService) GetReportPost(reporterID, reportedID string) (*model.ReportPost, error) {
	return r.repo.GetReportPost(reporterID, reportedID)
}

func (r reportService) GetReportUserPaginated(page, limit int) ([]*model.ReportUser, int64, error) {
	return r.repo.GetReportUserPaginated(page, limit)
}

func (r reportService) GetReportPostPaginated(page, limit int) ([]*model.ReportPost, int64, error) {
	return r.repo.GetReportPostPaginated(page, limit)
}

func (r reportService) CreateReportUser(req *transaction.ReportUserRequest, userID string) error {
	report := &model.ReportUser{
		ReporterID:  userID,
		ReportedID:  req.ReportedID,
		Description: req.Description,
	}
	return r.repo.CreateReportUser(report)
}

func (r reportService) CreateReportPost(req *transaction.ReportPostRequest, userID string) error {
	report := &model.ReportPost{
		ReporterID:  userID,
		ReportedID:  req.ReportedID,
		Description: req.Description,
	}
	return r.repo.CreateReportPost(report)
}

func NewReportService(repo repository.ReportRepository) ReportService {
	return &reportService{
		repo: repo,
	}
}
