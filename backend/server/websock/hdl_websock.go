package websock

import (
	"encoding/json"
	"log"
	"net/http"
	"sync"
	"time"

	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/service"
	"github.com/VoAnKhoi2005/ReSell/backend/server/util"
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
)

const (
	sendQueueLimit = 128
	writeWait      = 10 * time.Second
	pongWait       = 55 * time.Second
	pingPeriod     = (pongWait * 9) / 10
	maxMessageSize = 512
)

type WSHandler struct {
	messageService service.MessageService
	sessions       map[string]*Session
	mu             sync.RWMutex
}

func NewWSHandler(messageService service.MessageService) *WSHandler {
	return &WSHandler{
		messageService: messageService,
		sessions:       make(map[string]*Session),
	}
}

var upgrader = websocket.Upgrader{
	ReadBufferSize:    1024,
	WriteBufferSize:   1024,
	EnableCompression: true,
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

func (h *WSHandler) Handler(c *gin.Context) {
	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User authentication failed"})
		return
	}

	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Println("Upgrade error:", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "WebSocket upgrade failed"})
		return
	}

	session := &Session{
		UserID:     userID,
		WS:         conn,
		LastAction: time.Now().Unix(),
		Send:       make(chan any, sendQueueLimit+32),
		Stop:       make(chan any),
	}

	h.addSession(userID, session)

	go h.readLoop(session)
	go h.writeLoop(session)
}

func (h *WSHandler) readLoop(sess *Session) {
	defer func() {
		h.removeSession(sess.UserID)
		_ = sess.WS.Close()
	}()

	sess.WS.SetReadLimit(maxMessageSize)
	_ = sess.WS.SetReadDeadline(time.Now().Add(pongWait))
	sess.WS.SetPongHandler(func(string) error {
		_ = sess.WS.SetReadDeadline(time.Now().Add(pongWait))
		return nil
	})

	for {
		_, raw, err := sess.WS.ReadMessage()
		if err != nil {
			if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
				log.Printf("ws: unexpected close for user %s: %v", sess.UserID, err)
			}
			break
		}

		var incoming model.IncomingSocketMessage
		if err := json.Unmarshal(raw, &incoming); err != nil {
			h.sendError(sess, "Malformed socket message")
			continue
		}

		switch incoming.Type {
		case model.NewMessage:
			var payload model.SendMessagePayload
			if err := json.Unmarshal(incoming.Data, &payload); err != nil {
				log.Printf("ws: invalid send payload from user %s: %v", sess.UserID, err)
				continue
			}
			sess.LastAction = time.Now().Unix()
			go h.handleSendMessage(sess, &payload)

		case model.TypingIndicator:
			var payload *model.TypingPayload
			if err := json.Unmarshal(incoming.Data, payload); err != nil {
				log.Printf("ws: invalid typing payload from user %s: %v", sess.UserID, err)
				continue
			}
			sess.LastAction = time.Now().Unix()
			go h.handleTyping(sess, payload)

		default:
			h.sendError(sess, "Unknown message type: "+string(incoming.Type))
		}
	}
}

func (h *WSHandler) handleSendMessage(sess *Session, msg *model.SendMessagePayload) {
	message := model.Message{
		ConversationId: &msg.ConversationId,
		Content:        msg.Content,
		SenderId:       &sess.UserID,
		CreatedAt:      time.Now(),
	}

	// Save to database
	savedMsg, err := h.messageService.CreateMessage(&message)
	if err != nil {
		log.Printf("Error saving message for user %s: %v", sess.UserID, err)
		h.sendError(sess, "Failed to save message")
		return
	}

	conversation, err := h.messageService.GetConversationByID(*savedMsg.ConversationId)
	if err != nil {
		log.Printf("Error loading conversation %s: %v", *savedMsg.ConversationId, err)
		return
	}

	// Send confirmation to sender
	h.sendToSession(sess, map[string]interface{}{
		"type": model.MessageSend,
		"data": savedMsg,
	})

	// Broadcast to other participant
	response := map[string]interface{}{
		"type": model.NewMessage,
		"data": savedMsg,
	}

	var recipientID string
	if conversation.BuyerId != nil && *conversation.BuyerId != sess.UserID {
		recipientID = *conversation.BuyerId
	} else if conversation.SellerId != nil && *conversation.SellerId != sess.UserID {
		recipientID = *conversation.SellerId
	}

	if recipientID != "" {
		h.broadcastToUsers([]string{recipientID}, response)
	} else {
		log.Printf("No recipient found for conversation %s", *savedMsg.ConversationId)
		return
	}
}

func (h *WSHandler) handleTyping(sess *Session, payload *model.TypingPayload) {
	conversation, err := h.messageService.GetConversationByID(payload.ConversationId)
	if err != nil {
		log.Printf("Typing error: cannot find conversation %s", payload.ConversationId)
		return
	}

	response := map[string]interface{}{
		"type": model.TypingIndicator,
		"data": map[string]interface{}{
			"user_id":         sess.UserID,
			"conversation_id": payload.ConversationId,
			"is_typing":       payload.IsTyping,
		},
	}

	var recipientID string
	if conversation.BuyerId != nil && *conversation.BuyerId != sess.UserID {
		recipientID = *conversation.BuyerId
	} else if conversation.SellerId != nil && *conversation.SellerId != sess.UserID {
		recipientID = *conversation.SellerId
	}

	if recipientID != "" {
		h.broadcastToUsers([]string{recipientID}, response)
	}
}

func (h *WSHandler) sendError(sess *Session, errMsg string) {
	h.sendToSession(sess, map[string]interface{}{
		"type":  model.ErrorMessage,
		"error": errMsg,
	})
}

func (h *WSHandler) writeLoop(sess *Session) {
	ticker := time.NewTicker(pingPeriod)
	defer func() {
		ticker.Stop()
		_ = sess.WS.Close()
	}()

	for {
		select {
		case msg, ok := <-sess.Send:
			if !ok {
				return
			}
			if err := h.writeMessage(sess.WS, msg); err != nil {
				log.Printf("Write error for user %s: %v", sess.UserID, err)
				return
			}
		case <-sess.Stop:
			return
		case <-ticker.C:
			_ = sess.WS.SetWriteDeadline(time.Now().Add(writeWait))
			if err := sess.WS.WriteMessage(websocket.PingMessage, nil); err != nil {
				log.Printf("Ping failed for user %s: %v", sess.UserID, err)
				return
			}
		}
	}
}

func (h *WSHandler) addSession(userID string, session *Session) {
	h.mu.Lock()
	defer h.mu.Unlock()

	if existing, exists := h.sessions[userID]; exists {
		close(existing.Send)
		_ = existing.WS.Close()
	}
	h.sessions[userID] = session
}

func (h *WSHandler) removeSession(userID string) {
	h.mu.Lock()
	defer h.mu.Unlock()

	if session, exists := h.sessions[userID]; exists {
		close(session.Send)
		_ = session.WS.Close()
		delete(h.sessions, userID)
	}
}

func (h *WSHandler) broadcastToUsers(userIDs []string, message interface{}) {
	h.mu.RLock()
	defer h.mu.RUnlock()

	for _, userID := range userIDs {
		if session, exists := h.sessions[userID]; exists {
			select {
			case session.Send <- message:
			default:
				log.Printf("Channel full for user %s", userID)
			}
		}
	}
}

func (h *WSHandler) sendToSession(sess *Session, msg interface{}) {
	select {
	case sess.Send <- msg:
	default:
		log.Printf("Could not send to user %s - channel full", sess.UserID)
	}
}

func (h *WSHandler) writeMessage(ws *websocket.Conn, msg interface{}) error {
	var payload []byte
	switch v := msg.(type) {
	case []byte:
		payload = v
	case string:
		payload = []byte(v)
	default:
		var err error
		payload, err = json.Marshal(msg)
		if err != nil {
			return err
		}
	}
	_ = ws.SetWriteDeadline(time.Now().Add(writeWait))
	return ws.WriteMessage(websocket.TextMessage, payload)
}
