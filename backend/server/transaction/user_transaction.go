package transaction

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

type FollowRequest struct {
	FollowerID string `json:"follower_id" binding:"required"`
	FolloweeID string `json:"followee_id" binding:"required"`
}

type BanRequest struct {
	AdminID   string `json:"admin_id" binding:"required"`
	BanUserID string `json:"ban_user_id" binding:"required"`
	Length    uint   `json:"length" binding:"required"` //day
}
