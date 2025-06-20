package transaction

import "github.com/VoAnKhoi2005/ReSell/backend/server/model"

type CreateCommunityRequest struct {
	Name        string              `json:"name" binding:"required"`
	Description string              `json:"description" binding:"required"`
	Type        model.CommunityType `json:"type" binding:"required"`
}

type UpdateCommunityRequest struct {
	Name        string              `json:"name" binding:"required"`
	Description string              `json:"description" binding:"required"`
	Type        model.CommunityType `json:"type" binding:"required"`
}
