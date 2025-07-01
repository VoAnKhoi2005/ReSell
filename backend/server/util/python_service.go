package util

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/transaction"
	"net/http"
	"time"
)

const PythonServerBaseURL = "http://python:5000/"

func CallPythonScoreAPI(reqData *transaction.ScoringRequest) (*transaction.ScoringResponse, error) {
	url := PythonServerBaseURL + "score"

	var response *transaction.ScoringResponse
	jsonData, err := json.Marshal(reqData)
	if err != nil {
		return response, fmt.Errorf("marshal request: %w", err)
	}

	client := &http.Client{Timeout: 10 * time.Second}
	resp, err := client.Post(url, "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		return response, fmt.Errorf("post request: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return response, fmt.Errorf("bad status: %s", resp.Status)
	}

	err = json.NewDecoder(resp.Body).Decode(&response)
	if err != nil {
		return response, fmt.Errorf("decode response: %w", err)
	}

	return response, nil
}
