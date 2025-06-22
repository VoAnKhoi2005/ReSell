package dto

type UserStatDTO struct {
	PostNumber   uint `json:"post_number"`
	BoughtNumber uint `json:"bought_number"`
	SelledNumber uint `json:"selled_number"`
	ReportNumber uint `json:"report_number"`
	Revenue      uint `json:"revenue"`
}
