package data

import (
	"fmt"
	"github.com/VoAnKhoi2005/ReSell/backend/server/config"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"
)

func seedUser() []string {
	var userIDs []string
	var users []model.User
	for i := 1; i <= 100; i++ {
		id := uuid.New().String()

		password, _ := bcrypt.GenerateFromPassword([]byte("123456789"), bcrypt.DefaultCost)

		u := model.User{
			ID:       id,
			Username: fmt.Sprintf("user%d", i),
			Email:    ptr(fmt.Sprintf("user%d@gmail.com", i)),
			Password: string(password),
			Phone:    ptr("0123456789"),
			Fullname: fmt.Sprintf("Nguyen Van Nguoi Dung %d", i),
			Status:   model.ActiveStatus,
		}

		users = append(users, u)
		userIDs = append(userIDs, id)
	}

	config.DB.Create(&users)

	return userIDs
}

func ptr[T any](v T) *T {
	return &v
}
