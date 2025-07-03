package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/backend/server/dto"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
)

type MessageService interface {
	CreateConversation(conversation *model.Conversation) (*model.Conversation, error)
	DeleteConversation(userID string, conversationID string) error

	GetConversationByID(conversationId string) (*model.Conversation, error)
	GetConversationsByPostID(postID string) ([]*model.Conversation, error)
	GetConversationsByUserID(userID string) ([]*model.Conversation, error)
	GetConversationByUserAndPostID(userID string, postID string) (*model.Conversation, error)
	GetConversationStatDTOByUserID(conversationId string) ([]*dto.ConversationStatDTO, error)

	UpdateOffer(conversationID string, isSelling *bool, amount *int) (*model.Conversation, error)

	CreateMessage(message *model.Message) (*model.Message, error)
	GetMessageByID(messageId string) (*model.Message, error)
	GetMessagesInRange(senderID string, conversationID string, start uint, end uint) ([]*model.Message, error)
	GetLatestMessages(conversationID string, number uint) ([]*model.Message, error)
	GetLatestMessagesByBatch(conversationID string, batchSize int, page int) ([]*model.Message, int, error)
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

	if conversation.BuyerId == nil || conversation.SellerId == nil {
		return errors.New("invalid conversation: missing buyer or seller")
	}

	if *conversation.BuyerId != userID && *conversation.SellerId != userID {
		return errors.New("unauthorized: user cannot delete this conversation")
	}

	return m.messageRepository.DeleteConversation(conversation)
}

func (m *messageService) GetConversationByID(conversationId string) (*model.Conversation, error) {
	return m.messageRepository.GetConversationByID(conversationId)
}

func (m *messageService) GetConversationStatDTOByUserID(conversationId string) ([]*dto.ConversationStatDTO, error) {
	return m.messageRepository.GetConversationStatsByUserID(conversationId)
}

func (m *messageService) UpdateOffer(conversationID string, isSelling *bool, amount *int) (*model.Conversation, error) {
	conversation, err := m.messageRepository.GetConversationByID(conversationID)
	if err != nil {
		return nil, err
	}

	if amount != nil {
		if *amount <= 0 {
			return nil, errors.New("amount must be greater than zero")
		}

		conversation.Offer = amount
	}

	if isSelling != nil {
		conversation.IsSelling = *isSelling
	}

	return m.messageRepository.UpdateConversation(conversation)
}

func (m *messageService) GetConversationsByPostID(postID string) ([]*model.Conversation, error) {
	return m.messageRepository.GetConversationsByPostID(postID)
}

func (m *messageService) GetConversationsByUserID(userID string) ([]*model.Conversation, error) {
	return m.messageRepository.GetConversationsByUserID(userID)
}

func (m *messageService) GetConversationByUserAndPostID(userID string, postID string) (*model.Conversation, error) {
	return m.messageRepository.GetConversationByUserAndPostID(userID, postID)
}

func (m *messageService) CreateMessage(message *model.Message) (*model.Message, error) {
	conversation, err := m.messageRepository.GetConversationByID(*message.ConversationId)
	if err != nil {
		return nil, err
	}

	if *conversation.BuyerId != *message.SenderId && *conversation.SellerId != *message.SenderId {
		return nil, errors.New("not authorized")
	}

	return m.messageRepository.CreateMessage(message)
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

func (m *messageService) GetLatestMessagesByBatch(conversationID string, batchSize int, page int) ([]*model.Message, int, error) {
	if batchSize < 10 || batchSize > 1000 {
		return nil, 0, errors.New("batch size too large or too small")
	}

	if page < 1 {
		return nil, 0, errors.New("page must be greater than zero")
	}

	return m.messageRepository.GetLatestMessagesByBatch(conversationID, batchSize, page)
}
