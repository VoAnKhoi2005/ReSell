package transaction

type FollowRequest struct {
	FollowerID *string `form:"follower_id" json:"follower_id" binding:"required"`
	FolloweeID *string `form:"followee_id" json:"followee_id" binding:"required"`
}
