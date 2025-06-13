package websock

import (
	"encoding/json"
	"log"
	"net/http"
	"sync"
	"time"

	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/service"
	"github.com/VoAnKhoi2005/ReSell/util"
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
	sessions       map[string]*model.Session
	mu             sync.RWMutex
}

func NewWSHandler(messageService service.MessageService) *WSHandler {
	return &WSHandler{
		messageService: messageService,
		sessions:       make(map[string]*model.Session),
	}
}

var upgrader = websocket.Upgrader{
	ReadBufferSize:    1024,
	WriteBufferSize:   1024,
	EnableCompression: true,
	CheckOrigin: func(r *http.Request) bool {
		return true // Adjust for production
	},
}

func (h *WSHandler) Handler(c *gin.Context) {
	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "WebSocket upgrade failed"})
		return
	}

	userID, err := util.GetUserID(c)
	if err != nil || userID == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "User authentication failed"})
		return
	}

	session := &model.Session{
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

func (h *WSHandler) readLoop(sess *model.Session) {
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

		var msg model.Message
		if err := json.Unmarshal(raw, &msg); err != nil {
			log.Printf("ws: message parse error for user %s: %v", sess.UserID, err)
			continue
		}

		go h.handleMessage(sess, &msg)
	}
}

func (h *WSHandler) handleMessage(sess *model.Session, msg *model.Message) {
	msg.SenderId = &sess.UserID

	// Save to database
	savedMsg, err := h.messageService.CreateMessage(msg)
	if err != nil {
		log.Printf("Error saving message for user %s: %v", sess.UserID, err)
		h.sendToSession(sess, map[string]interface{}{
			"type":  "error",
			"error": "Failed to save message",
		})
		return
	}

	conversation, err := h.messageService.GetConversationByID(*savedMsg.ConversationId)
	if err != nil {
		log.Printf("Error loading conversation %s: %v", *msg.ConversationId, err)
		return
	}

	response := map[string]interface{}{
		"type": "new_message",
		"data": map[string]interface{}{
			"id":              savedMsg.ID,
			"content":         savedMsg.Content,
			"sender_id":       savedMsg.SenderId,
			"created_at":      savedMsg.CreatedAt,
			"conversation_id": savedMsg.ConversationId,
		},
	}

	// Send confirmation to sender
	h.sendToSession(sess, map[string]interface{}{
		"type": "message_delivered",
		"id":   savedMsg.ID,
	})

	// Broadcast to other participant
	var recipientID string
	if conversation.BuyerId != nil && *conversation.BuyerId != sess.UserID {
		recipientID = *conversation.BuyerId
	} else if conversation.SellerId != nil {
		recipientID = *conversation.SellerId
	}

	if recipientID != "" {
		h.broadcastToUsers([]string{recipientID}, response)
	}
}

func (h *WSHandler) writeLoop(sess *model.Session) {
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

func (h *WSHandler) addSession(userID string, session *model.Session) {
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

func (h *WSHandler) sendToSession(sess *model.Session, msg interface{}) {
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
