package repository

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/util"
	"gorm.io/gorm"
)

type MessageRepository interface {
	CreateConversation(conversation *model.Conversation) error
	DeleteConversation(conversation *model.Conversation) error

	GetConversationByID(conversationId string) (*model.Conversation, error)
	GetConversationsByPostID(postID string) ([]*model.Conversation, error)

	Create(message *model.Message) error
	Update(message *model.Message) error
	Delete(message *model.Message) error
	GetAll() ([]*model.Message, error)
	GetMessageByID(messageId string) (*model.Message, error)

	GetMessagesInRange(conversationID string, start uint, end uint) ([]*model.Message, error)
	GetLatestMessages(conversationID string, number uint) ([]*model.Message, error)
}

type messageRepository struct {
	*BaseRepository[model.Message]
}

func NewMessageRepository(db *gorm.DB) MessageRepository {
	return &messageRepository{
		BaseRepository: NewBaseRepository[model.Message](db),
	}
}

func (m *messageRepository) CreateConversation(conversation *model.Conversation) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return m.db.WithContext(ctx).Create(conversation).Error
}

func (m *messageRepository) DeleteConversation(conversation *model.Conversation) error {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	return m.db.WithContext(ctx).Delete(conversation).Error
}

func (m *messageRepository) GetConversationByID(conversationId string) (*model.Conversation, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var conversation model.Conversation
	err := m.db.WithContext(ctx).First(&conversation, "id = ?", conversationId).Error
	return &conversation, err
}

func (m *messageRepository) GetConversationsByPostID(postID string) ([]*model.Conversation, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var conversations []*model.Conversation
	err := m.db.WithContext(ctx).First(&conversations, "post_id = ?", postID).Error
	return conversations, err
}

func (m *messageRepository) GetMessageByID(messageId string) (*model.Message, error) {
	ctx, cancel := util.NewDBContext()
	defer cancel()

	var message model.Message
	err := m.db.WithContext(ctx).First(&message, "id = ?", messageId).Error
	return &message, err
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
