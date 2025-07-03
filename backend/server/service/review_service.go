package service

import (
	"errors"
	"github.com/VoAnKhoi2005/ReSell/backend/server/fb"
	"github.com/VoAnKhoi2005/ReSell/backend/server/model"
	"github.com/VoAnKhoi2005/ReSell/backend/server/repository"
)

type ReviewService interface {
	CreateReview(review *model.UserReview) error
	GetReviewByBuyerID(buyerID string) ([]*model.UserReview, error)
	GetReviewByOrderID(sellerID string) (*model.UserReview, error)
	GetReviewByPostID(postID string) (*model.UserReview, error)
	DeleteReviewByOrderID(userID string, orderID string) error

	GetSellerID(orderID string) (string, error)
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

	if order.Status != model.OrderStatusCompleted {
		return errors.New("order status is invalid for creating review")
	}
	err = r.reviewRepository.CreateReview(review)
	if err != nil {
		return err
	}

	//Handle notification
	sellerID, err := r.GetSellerID(*review.OrderId)
	if err != nil {
		return err
	}
	title := "You Got a New Review!"
	description := "A buyer just left feedback on your post."
	err = fb.FcmHandler.SendNotification(sellerID, title, description, false, model.DefaultNotification)

	//Reputation
	err = repository.GlobalRepo.IncreaseReputation(sellerID, int(5*review.Rating))
	if err != nil {
		return err
	}

	return nil
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

func (r *reviewService) GetSellerID(orderID string) (string, error) {
	return r.orderRepository.GetSellerID(orderID)
}
