package transaction

type FollowRequest struct {
	FollowerID *string `form:"follower_id" json:"follower_id" binding:"required"`
	FolloweeID *string `form:"followee_id" json:"followee_id" binding:"required"`
}

type BanRequest struct {
	AdminID   string `form:"admin_id" json:"admin_id" binding:"required"`
	BanUserID string `form:"ban_user_id" json:"ban_user_id" binding:"required"`
	Length    uint   `form:"length" json:"length" binding:"required"` //in day
}
