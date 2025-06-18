package data

import (
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	lorem "github.com/drhodes/golorem"
	"github.com/google/uuid"
)

// report user random, co gioi han
func seedReportUser(userIDs []string) {
	var reports []model.ReportUser

	shuffledUserIDs := shuffleStrings(userIDs)

	for i := 0; i < 20; i++ {
		report := model.ReportUser{
			ID:          uuid.New().String(),
			ReporterID:  shuffledUserIDs[i],
			ReportedID:  shuffledUserIDs[i+1],
			Description: lorem.Paragraph(2, 4),
		}
		reports = append(reports, report)
	}
	config.DB.Create(&reports)
}

func seedReportPost(userIDs, postIDs []string) {
	shuffledUserIDs := shuffleStrings(userIDs)
	shuffledPostIDs := shuffleStrings(postIDs)

	var reports []model.ReportPost

	for i := 0; i < 20; i++ {
		var post model.Post
		config.DB.First(&post, "id = ?", shuffledPostIDs[i])

		if *post.UserID == shuffledUserIDs[i] {
			continue
		}

		report := model.ReportPost{
			ID:          uuid.New().String(),
			ReporterID:  shuffledUserIDs[i],
			ReportedID:  post.ID,
			Description: lorem.Paragraph(2, 4),
		}

		reports = append(reports, report)
	}
	config.DB.Create(&reports)
}
