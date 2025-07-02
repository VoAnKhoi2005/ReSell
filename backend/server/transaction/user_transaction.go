package transaction

type ChangePasswordRequest struct {
	OldPassword string `json:"old_password" binding:"required"`
	NewPassword string `json:"new_password" binding:"required"`
}

type UpdateUserRequest struct {
	FullName *string `json:"fullname,omitempty"`
	Email    *string `json:"email,omitempty"`
	Phone    *string `json:"phone,omitempty"`
}

type BanRequest struct {
	BanUserID string `json:"ban_user_id" binding:"required"`
	Length    uint   `json:"length" binding:"required"` //day
}
