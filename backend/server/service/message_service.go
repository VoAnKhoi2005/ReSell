package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
)

type MessageService interface {
	CreateConversation(conversation *model.Conversation) (*model.Conversation, error)
	DeleteConversation(userID string, conversationID string) error
	GetConversationByID(conversationId string) (*model.Conversation, error)
	GetConversationsByPostID(postID string) ([]*model.Conversation, error)

	CreateMessage(message *model.Message) (*model.Message, error)
	GetMessageByID(messageId string) (*model.Message, error)
	GetMessagesInRange(senderID string, conversationID string, start uint, end uint) ([]*model.Message, error)
	GetLatestMessages(conversationID string, number uint) ([]*model.Message, error)
}

type messageService struct {
	messageRepository repository.MessageRepository
}

func NewMessageService(messageRepo repository.MessageRepository) MessageService {
	return &messageService{messageRepository: messageRepo}
}

func (m *messageService) CreateConversation(conversation *model.Conversation) (*model.Conversation, error) {
	return m.messageRepository.CreateConversation(conversation)
}

func (m *messageService) DeleteConversation(userID string, conversationID string) error {
	conversation, err := m.messageRepository.GetConversationByID(conversationID)
	if err != nil {
		return err
	}

	if *conversation.BuyerId != userID || *conversation.SellerId != userID {
		return errors.New("unauthorized. cannot delete conversation")
	}

	return m.messageRepository.DeleteConversation(conversation)
}

func (m *messageService) GetConversationByID(conversationId string) (*model.Conversation, error) {
	return m.messageRepository.GetConversationByID(conversationId)
}

func (m *messageService) GetConversationsByPostID(postID string) ([]*model.Conversation, error) {
	return m.messageRepository.GetConversationsByPostID(postID)
}

func (m *messageService) GetMessageByID(messageId string) (*model.Message, error) {
	return m.messageRepository.GetMessageByID(messageId)
}

func (m *messageService) GetMessagesInRange(senderID string, conversationID string, start uint, end uint) ([]*model.Message, error) {
	conversation, err := m.messageRepository.GetConversationByID(conversationID)
	if err != nil {
		return nil, err
	}

	if *conversation.BuyerId != senderID && *conversation.SellerId != senderID {
		return nil, errors.New("not authorized")
	}

	return m.messageRepository.GetMessagesInRange(conversationID, start, end)
}

func (m *messageService) GetLatestMessages(conversationID string, number uint) ([]*model.Message, error) {
	if number < 1 {
		return nil, errors.New("amount must be greater than zero")
	}

	return m.messageRepository.GetLatestMessages(conversationID, number)
}

func (m *messageService) CreateMessage(message *model.Message) (*model.Message, error) {
	conversation, err := m.messageRepository.GetConversationByID(*message.ConversationId)
	if err != nil {
		return nil, err
	}

	if conversation.BuyerId != message.SenderId && conversation.SellerId != message.SenderId {
		return nil, errors.New("not authorized")
	}

	return m.messageRepository.CreateMessage(message)
}
