# Stage 1: build app Go
FROM golang:1.24 AS builder

WORKDIR /app

COPY go.mod go.sum ./

RUN go mod download

COPY . .

RUN go build -o main .

#Stage 2: run app with lightweight distro
FROM debian:bookworm-slim

WORKDIR /app

COPY --from=builder /app/main .

COPY .env .env

EXPOSE 8080

CMD ["./main"]
