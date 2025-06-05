package config

import (
	"github.com/VoAnKhoi2005/ReSell/util"
	"github.com/redis/go-redis/v9"
	"log"
	"os"
	"strconv"
)

var RedisClient *redis.Client

func InitRedis() {
	addr := os.Getenv("REDIS_ADDR")
	password := os.Getenv("REDIS_PASSWORD")
	dbStr := os.Getenv("REDIS_DB")

	db, err := strconv.Atoi(dbStr)
	if err != nil {
		db = 0
	}

	RedisClient = redis.NewClient(&redis.Options{
		Addr:     addr,
		Password: password, // no password set
		DB:       db,       // use default DB
	})

	ctx, cancel := util.NewRedisContext()
	defer cancel()

	if _, err := RedisClient.Ping(ctx).Result(); err != nil {
		log.Fatalf("Could not connect to Redis: %v", err)
	}

}
