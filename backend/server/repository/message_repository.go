package repository

import (
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
	GetConversationByUserAndPostID(userID string, postID string) (*model.Conversation, error)

	CreateMessage(message *model.Message) (*model.Message, error)
	GetAllMessage(conversationID string) ([]*model.Message, error)
	GetMessageByID(messageId string) (*model.Message, error)

	GetMessagesInRange(conversationID string, start uint, end uint) ([]*model.Message, error)
	GetLatestMessages(conversationID string, number uint) ([]*model.Message, error)
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
	err := m.db.WithContext(ctx).First(&conversation, "id = ?", conversationId).Error
	return conversation, err
}

func (m *messageRepository) GetConversationsByPostID(postID string) ([]*model.Conversation, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var conversations []*model.Conversation
	err := m.db.WithContext(ctx).First(&conversations, "post_id = ?", postID).Error
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
		Where("conversation_id = ?", conversationID).Order("created_at desc").
		Limit(int(number)).Find(&messages).Error
	return messages, err
}
