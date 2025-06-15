package transaction

type CreateDistrictRequest struct {
	Name       string `json:"name" binding:"required"`
	ProvinceID string `json:"province_id" binding:"required"`
}

type CreateWardRequest struct {
	Name       string `json:"name" binding:"required"`
	DistrictID string `json:"district_id" binding:"required"`
}

type CreateAddressRequest struct {
	WardID    string `json:"ward_id" binding:"required"`
	Detail    string `json:"detail" binding:"required"`
	IsDefault bool   `json:"is_default" binding:"required"`
}

type FailCreateResponse struct {
	Name    string `json:"name"`
	Message string `json:"message"`
}

type UpdateAddressRequest struct {
	WardID    *string `json:"ward_id"`
	Detail    *string `json:"detail"`
	IsDefault *bool   `json:"is_default"`
}
