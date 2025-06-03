package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/model"
	"github.com/VoAnKhoi2005/ReSell/repository"
)

type ReviewService interface {
	CreateReview(review *model.UserReview) error
	GetReviewByBuyerID(buyerID string) ([]*model.UserReview, error)
	GetReviewByOrderID(sellerID string) (*model.UserReview, error)
	GetReviewByPostID(postID string) (*model.UserReview, error)
	DeleteReviewByOrderID(userID string, orderID string) error
}

type reviewService struct {
	reviewRepository repository.ReviewRepository
	orderRepository  repository.OrderRepository
}

func NewReviewService(reviewRepo repository.ReviewRepository, orderRepo repository.OrderRepository) ReviewService {
	return &reviewService{
		reviewRepository: reviewRepo,
		orderRepository:  orderRepo,
	}
}

func (r *reviewService) CreateReview(review *model.UserReview) error {
	order, err := r.orderRepository.GetByID(*review.OrderId)
	if err != nil {
		return err
	}

	if *order.UserId != *review.UserId {
		return errors.New("unauthorized to review order")
	}

	if order.Status != model.OrderStatusSold {
		return errors.New("order status is invalid for creating review")
	}
	return r.reviewRepository.CreateReview(review)
}

func (r *reviewService) GetReviewByBuyerID(buyerID string) ([]*model.UserReview, error) {
	orders, err := r.orderRepository.GetByBuyerID(buyerID)
	if err != nil {
		return nil, err
	}

	var reviews []*model.UserReview
	for _, order := range orders {
		review, err := r.reviewRepository.GetReviewByOrderID(order.ID)
		if err != nil {
			return nil, err
		}
		reviews = append(reviews, review)
	}

	return reviews, nil
}

func (r *reviewService) GetReviewByOrderID(sellerID string) (*model.UserReview, error) {
	return r.reviewRepository.GetReviewByOrderID(sellerID)
}

func (r *reviewService) GetReviewByPostID(postID string) (*model.UserReview, error) {
	order, err := r.orderRepository.GetByPostID(postID)
	if err != nil {
		return nil, err
	}

	return r.reviewRepository.GetReviewByOrderID(order.ID)
}

func (r *reviewService) DeleteReviewByOrderID(userID string, orderID string) error {
	order, err := r.orderRepository.GetByID(orderID)
	if err != nil {
		return err
	}

	if *order.UserId != userID {
		return errors.New("unauthorized to delete review")
	}

	review, err := r.reviewRepository.GetReviewByOrderID(order.ID)
	if err != nil {
		return err
	}

	return r.reviewRepository.DeleteReview(review)
}
