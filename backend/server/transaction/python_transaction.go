package transaction

import "github.com/VoAnKhoi2005/ReSell/backend/server/dto"

type ScoringRequest struct {
	PostData     dto.PostDataForScoring `json:"post"`
	BuyerProfile dto.BuyerProfile       `json:"buyer"`
}

type ScoringResponse struct {
	PostID                    string  `json:"post_id"`
	TitleKeywordOverlap       float64 `json:"title_keyword_overlap"`
	DescriptionKeywordOverlap float64 `json:"description_keyword_overlap"`
	ImageTextScore            float64 `json:"image_text_score"`
}
