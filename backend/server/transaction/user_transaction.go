package transaction

type FollowRequest struct {
	FollowerID string `json:"follower_id" binding:"required"`
	FolloweeID string `json:"followee_id" binding:"required"`
}

type BanRequest struct {
	AdminID   string `json:"admin_id" binding:"required"`
	BanUserID string `json:"ban_user_id" binding:"required"`
	Length    uint   `json:"length" binding:"required"` //day
}
