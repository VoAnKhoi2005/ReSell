package service

import (
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
)

type MessageService interface {
	CreateConversation(conversation *model.Conversation) error
	DeleteConversation(conversation *model.Conversation) error
	GetConversationByID(conversationId string) (*model.Conversation, error)
	GetConversationsByPostID(postID string) ([]*model.Conversation, error)

	CreateMessage(message *model.Message) error
	GetMessageByID(messageId string) (*model.Message, error)
	GetMessagesInRange(conversationID string, start uint, end uint) ([]*model.Message, error)
	GetLatestMessages(conversationID string, number uint) ([]*model.Message, error)
}

type messageService struct {
	messageRepository repository.MessageRepository
}

func NewMessageService(messageRepo repository.MessageRepository) MessageService {
	return &messageService{messageRepository: messageRepo}
}

func (m messageService) CreateConversation(conversation *model.Conversation) error {
	return m.messageRepository.CreateConversation(conversation)
}

func (m messageService) DeleteConversation(conversation *model.Conversation) error {
	return m.messageRepository.DeleteConversation(conversation)
}

func (m messageService) GetConversationByID(conversationId string) (*model.Conversation, error) {
	return m.messageRepository.GetConversationByID(conversationId)
}

func (m messageService) GetConversationsByPostID(postID string) ([]*model.Conversation, error) {
	return m.messageRepository.GetConversationsByPostID(postID)
}

func (m messageService) GetMessageByID(messageId string) (*model.Message, error) {
	return m.messageRepository.GetMessageByID(messageId)
}

func (m messageService) GetMessagesInRange(conversationID string, start uint, end uint) ([]*model.Message, error) {
	return m.messageRepository.GetMessagesInRange(conversationID, start, end)
}

func (m messageService) GetLatestMessages(conversationID string, number uint) ([]*model.Message, error) {
	return m.messageRepository.GetLatestMessages(conversationID, number)
}

func (m messageService) CreateMessage(message *model.Message) error {
	return m.messageRepository.Create(message)
}
