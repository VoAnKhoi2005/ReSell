package transaction

type GetAllUserRequest struct {
	BatchSize int `json:"batch_size" binding:"required"`
	Page      int `json:"page" binding:"required"`
}

type ChangePasswordRequest struct {
	OldPassword string `json:"old_password" binding:"required"`
	NewPassword string `json:"new_password" binding:"required"`
}

type UpdateUserRequest struct {
	Username  *string `json:"username"`
	Email     *string `json:"email"`
	Phone     *string `json:"phone"`
	FullName  *string `json:"full_name"`
	CitizenId *string `json:"citizen_id"`
}

type BanRequest struct {
	BanUserID string `json:"ban_user_id" binding:"required"`
	Length    uint   `json:"length" binding:"required"` //day
}
