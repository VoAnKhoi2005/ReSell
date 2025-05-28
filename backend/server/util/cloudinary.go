package util

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"mime/multipart"
	"net/http"
	"os"
	"path/filepath"
	"strings"
)

type CloudinaryResponse struct {
	URL string `json:"secure_url"`
}

// UploadToCloudinary uploads an image file to Cloudinary and returns the image URL
func UploadToCloudinary(file multipart.File, fileHeader *multipart.FileHeader) (string, error) {
	var b bytes.Buffer
	writer := multipart.NewWriter(&b)

	//Add file field
	part, err := writer.CreateFormFile("file", fileHeader.Filename)
	if err != nil {
		return "", err
	}

	_, err = io.Copy(part, file)

	//Add other fields
	writer.WriteField("folder", os.Getenv("CLOUDINARY_FOLDER"))
	writer.WriteField("upload_preset", "resell_preset")

	writer.Close()

	//Build transaction
	url := fmt.Sprintf("https://api.cloudinary.com/v1_1/%s/image/upload", os.Getenv("CLOUDINARY_CLOUD_NAME"))
	req, err := http.NewRequest("POST", url, &b)
	if err != nil {
		return "", err
	}
	req.SetBasicAuth(os.Getenv("CLOUDINARY_API_KEY"), os.Getenv("CLOUDINARY_API_SECRET"))
	req.Header.Set("Content-Type", writer.FormDataContentType())

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	//Parse response
	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return "", fmt.Errorf("cloudinary error: %s", string(bodyBytes))
	}

	var cloudRes CloudinaryResponse
	if err := json.NewDecoder(resp.Body).Decode(&cloudRes); err != nil {
		return "", err
	}
	return cloudRes.URL, nil
}

func ExtractPublicID(imageURL string) string {
	// Ví dụ: https://res.cloudinary.com/demo/image/upload/v123456/folder/image123.jpg
	parts := strings.Split(imageURL, "/")
	publicIDWithExt := parts[len(parts)-1]
	folder := parts[len(parts)-2]
	publicID := folder + "/" + strings.TrimSuffix(publicIDWithExt, filepath.Ext(publicIDWithExt))
	return publicID
}

func DeleteFromCloudinary(publicID string) error {
	url := fmt.Sprintf("https://api.cloudinary.com/v1_1/%s/image/destroy", os.Getenv("CLOUDINARY_CLOUD_NAME"))

	payload := map[string]string{
		"public_id": publicID,
	}
	body, _ := json.Marshal(payload)

	req, _ := http.NewRequest("POST", url, bytes.NewReader(body))
	req.SetBasicAuth(os.Getenv("CLOUDINARY_API_KEY"), os.Getenv("CLOUDINARY_API_SECRET"))
	req.Header.Set("Content-Type", "application/json")

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		data, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("cloudinary error: %s", data)
	}
	return nil
}
