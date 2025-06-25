package repository

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"gorm.io/gorm"
)

type MessageRepository interface {
	CreateConversation(conversation *model.Conversation) (*model.Conversation, error)
	DeleteConversation(conversation *model.Conversation) error

	GetConversationByID(conversationId string) (*model.Conversation, error)
	GetConversationsByPostID(postID string) ([]*model.Conversation, error)
	GetConversationsByUserID(userId string) ([]*model.Conversation, error)
	GetConversationStatsByUserID(userID string) ([]*dto.ConversationStatDTO, error)
	GetConversationByUserAndPostID(userID string, postID string) (*model.Conversation, error)

	CreateMessage(message *model.Message) (*model.Message, error)
	GetAllMessage(conversationID string) ([]*model.Message, error)
	GetMessageByID(messageId string) (*model.Message, error)

	GetMessagesInRange(conversationID string, start uint, end uint) ([]*model.Message, error)
	GetLatestMessages(conversationID string, number uint) ([]*model.Message, error)
	GetLatestMessagesByBatch(conversationID string, batchSize int, page int) ([]*model.Message, int, error)
}

type messageRepository struct {
	db *gorm.DB
}

func NewMessageRepository(db *gorm.DB) MessageRepository {
	return &messageRepository{db: db}
}

func (m *messageRepository) CreateConversation(conversation *model.Conversation) (*model.Conversation, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	if err := m.db.WithContext(ctx).Create(&conversation).Error; err != nil {
		return nil, err
	}

	return conversation, nil
}

func (m *messageRepository) DeleteConversation(conversation *model.Conversation) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return m.db.WithContext(ctx).Delete(conversation).Error
}

func (m *messageRepository) GetConversationByID(conversationId string) (*model.Conversation, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var conversation *model.Conversation = nil
	err := m.db.WithContext(ctx).
		Preload("Post.PostImages").
		First(&conversation, "id = ?", conversationId).Error
	return conversation, err
}

func (m *messageRepository) GetConversationStatsByUserID(userID string) ([]*dto.ConversationStatDTO, error) {
	var results []*dto.ConversationStatDTO

	subQuery := m.db.
		Table("messages").
		Select("DISTINCT ON (conversation_id) conversation_id, content, created_at").
		Order("conversation_id, created_at DESC")

	err := m.db.
		Table("conversations").
		Select(`
			conversations.id AS conversation_id,
			seller.id AS seller_id,
			seller.username AS seller_username,
			seller.avatar_url AS seller_avatar,
			buyer.id AS buyer_id,
			buyer.username AS buyer_username,
			buyer.avatar_url AS buyer_avatar,
			posts.id AS post_id,
			posts.title AS post_title,
			post_images.image_url AS post_thumbnail,
			messages.content AS last_message,
			messages.created_at AS last_updated_at,
			conversations.created_at
		`).
		Joins("LEFT JOIN users AS seller ON seller.id = conversations.seller_id").
		Joins("LEFT JOIN users AS buyer ON buyer.id = conversations.buyer_id").
		Joins("LEFT JOIN posts ON posts.id = conversations.post_id").
		Joins("LEFT JOIN post_images ON post_images.post_id = posts.id").
		Joins("LEFT JOIN (?) AS messages ON messages.conversation_id = conversations.id", subQuery).
		Where("conversations.buyer_id = ? OR conversations.seller_id = ?", userID, userID).
		Group("conversations.id, seller.id, buyer.id, posts.id, post_images.image_url, messages.content, messages.created_at").
		Scan(&results).Error

	return results, err
}

func (m *messageRepository) GetConversationsByPostID(postID string) ([]*model.Conversation, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var conversations []*model.Conversation
	err := m.db.WithContext(ctx).
		Preload("Post.PostImages").
		First(&conversations, "post_id = ?", postID).Error
	return conversations, err
}

func (m *messageRepository) GetConversationsByUserID(userId string) ([]*model.Conversation, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var conversations []*model.Conversation
	err := m.db.WithContext(ctx).Find(&conversations, "seller_id = ? OR buyer_id = ?", userId, userId).Error
	return conversations, err
}

func (m *messageRepository) GetConversationByUserAndPostID(userID string, postID string) (*model.Conversation, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var conversation *model.Conversation = nil
	err := m.db.WithContext(ctx).First(&conversation, "(buyer_id = ? OR seller_id = ?) AND post_id = ?",
		userID, userID, postID).Error
	return conversation, err
}

func (m *messageRepository) CreateMessage(message *model.Message) (*model.Message, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	if err := m.db.WithContext(ctx).Create(&message).Error; err != nil {
		return nil, err
	}

	return message, nil
}

func (m *messageRepository) GetAllMessage(conversationID string) ([]*model.Message, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var messages []*model.Message
	err := m.db.WithContext(ctx).Find(&messages, "conversation_id = ?", conversationID).Error
	return messages, err
}

func (m *messageRepository) GetMessageByID(messageId string) (*model.Message, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var message *model.Message = nil
	err := m.db.WithContext(ctx).First(&message, "id = ?", messageId).Error
	return message, err
}

func (m *messageRepository) GetMessagesInRange(conversationID string, start uint, end uint) ([]*model.Message, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var messages []*model.Message

	limit := int(end - start + 1)
	offset := int(start)

	err := m.db.WithContext(ctx).
		Where("conversation_id = ?", conversationID).Order("created_at desc").
		Offset(offset).Limit(limit).
		Find(&messages).Error

	return messages, err
}

func (m *messageRepository) GetLatestMessages(conversationID string, number uint) ([]*model.Message, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var messages []*model.Message
	err := m.db.WithContext(ctx).
		Where("conversation_id = ?", conversationID).
		Order("created_at desc").
		Limit(int(number)).Find(&messages).Error
	return messages, err
}

func (m *messageRepository) GetLatestMessagesByBatch(conversationID string, batchSize int, page int) ([]*model.Message, int, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var totalCount int64
	err := m.db.WithContext(ctx).
		Model(&model.Message{}).
		Where("conversation_id = ?", conversationID).
		Count(&totalCount).Error
	if err != nil {
		return nil, 0, err
	}

	totalBatches := int((totalCount + int64(batchSize) - 1) / int64(batchSize))
	offset := (page - 1) * batchSize

	if totalBatches == 0 {
		totalBatches = 1
	}

	if page > totalBatches {
		return nil, totalBatches, fmt.Errorf("page %d out of range: total pages %d", page, totalBatches)
	}

	var messages []*model.Message
	err = m.db.WithContext(ctx).
		Where("conversation_id = ?", conversationID).
		Order("created_at desc").
		Limit(batchSize).Offset(offset).
		Find(&messages).Error
	return messages, totalBatches, err
}
