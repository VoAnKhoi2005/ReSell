package zalo

import (
	"bytes"
	"crypto/hmac"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"net/http"
	"time"
)

var callbackURL string

func SetCallbackURL(url string) {
	if url == "" {
		callbackURL = "http://localhost/api/order/payment/callback"
	}
	callbackURL = url + "/api/order/payment/callback"
}

const (
	AppID     = 2554 // Thay bằng app_id của mày
	Key1      = "sdngKKJmqEMzvh5QQcdD2A9XBSKUNaYn"
	Key2      = "trMrHtvjo6myautxDUiAcYsVtaeQ8nhf"
	CreateURL = "https://sb-openapi.zalopay.vn/v2/create"
)

type createOrderRequest struct {
	AppID       int    `json:"app_id"`
	AppUser     string `json:"app_user"`
	AppTransID  string `json:"app_trans_id"`
	AppTime     int64  `json:"app_time"`
	Item        string `json:"item"`
	Amount      int    `json:"amount"`
	Description string `json:"description"`
	EmbedData   string `json:"embed_data"`
	BankCode    string `json:"bank_code"`
	Mac         string `json:"mac"`
	CallbackURL string `json:"callback_url"`
}

type createOrderResponse struct {
	ReturnCode int    `json:"return_code"`
	OrderURL   string `json:"order_url"`
	ZPTransID  string `json:"zp_trans_token"` // optional
	Message    string `json:"return_message"`
}

func CreateZaloPayOrder(appTransID, appUser string, amount int) (string, string, error) {
	now := time.Now().UnixMilli()

	req := createOrderRequest{
		AppID:       AppID,
		AppUser:     appUser,
		AppTransID:  appTransID,
		AppTime:     now,
		Item:        "[]",
		Amount:      amount,
		Description: "Thanh toán đơn hàng ReSell",
		EmbedData:   "{}",
		BankCode:    "zalopayapp",
		CallbackURL: callbackURL,
	}

	log.Println("Zalo Callback URL:", callbackURL)

	// MAC: app_id|app_trans_id|app_user|amount|app_time|embed_data|item
	rawMac := fmt.Sprintf("%d|%s|%s|%d|%d|%s|%s", req.AppID, req.AppTransID, req.AppUser, req.Amount, req.AppTime, req.EmbedData, req.Item)
	req.Mac = generateMac(rawMac, Key1)

	body, _ := json.Marshal(req)

	resp, err := http.Post(CreateURL, "application/json", bytes.NewBuffer(body))
	if err != nil {
		return "", "", err
	}
	defer resp.Body.Close()

	var result createOrderResponse
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return "", "", err
	}

	if result.ReturnCode != 1 {
		return "", "", errors.New("ZaloPay error: " + result.Message)
	}

	return result.OrderURL, result.ZPTransID, nil
}

func generateMac(data, key string) string {
	h := hmac.New(sha256.New, []byte(key))
	h.Write([]byte(data))
	return hex.EncodeToString(h.Sum(nil))
}

func GenerateAppTransID() string {
	datePrefix := time.Now().Format("060102") // yyMMdd
	unique := time.Now().UnixNano() % 1000000
	return fmt.Sprintf("%s_%06d", datePrefix, unique)
}

func QueryOrderStatus(appTransID string) (bool, string, error) {
	url := "https://sb-openapi.zalopay.vn/v2/query"

	// MAC input: app_id|app_trans_id|key1
	data := fmt.Sprintf("%d|%s|%s", AppID, appTransID, Key1)
	mac := generateMac(data, Key1)

	payload := map[string]string{
		"app_id":       fmt.Sprintf("%d", AppID),
		"app_trans_id": appTransID,
		"mac":          mac,
	}
	body, _ := json.Marshal(payload)

	resp, err := http.Post(url, "application/json", bytes.NewBuffer(body))
	if err != nil {
		return false, "", err
	}
	defer resp.Body.Close()

	var res struct {
		ReturnCode  int    `json:"return_code"`
		ReturnMsg   string `json:"return_message"`
		Amount      int    `json:"amount"`
		Status      string `json:"status"` // "1" nếu thành công
		ZaloTransID string `json:"zp_trans_id"`
	}
	if err := json.NewDecoder(resp.Body).Decode(&res); err != nil {
		return false, "", err
	}

	if res.ReturnCode != 1 {
		return false, "", errors.New(res.ReturnMsg)
	}

	return res.Status == "1", res.ZaloTransID, nil
}
