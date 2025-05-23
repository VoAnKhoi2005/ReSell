package transaction

type CreateDistrictRequest struct {
	Name       string `json:"name" binding:"required"`
	ProvinceID string `json:"province_id" binding:"required"`
}

type CreateWardRequest struct {
	Name       string `json:"name" binding:"required"`
	DistrictID string `json:"district_id" binding:"required"`
}

type FailCreateRequest struct {
	ID      string `json:"id"`
	Message string `json:"message"`
}
