package transaction

type ReportUserRequest struct {
	ReportedID  string `json:"reported_id"`
	Description string `json:"description"`
}

type ReportPostRequest struct {
	ReportedID  string `json:"reported_id"`
	Description string `json:"description"`
}
