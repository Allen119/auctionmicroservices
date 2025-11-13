package com.onepiece.reviewservice.service.impl;

import com.onepiece.reviewservice.dto.ReviewRequestDTO;
import com.onepiece.reviewservice.dto.ReviewResponseDTO;
import com.onepiece.reviewservice.model.Review;
import com.onepiece.reviewservice.repository.ReviewRepository;
import com.onepiece.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    public final ReviewRepository reviewRepository;

    @Override
    public ReviewResponseDTO createReview(ReviewRequestDTO reviewRequestDTO) {
        Review revEntity =new Review(
                reviewRequestDTO.getBuyerId(),
                reviewRequestDTO.getSellerId(),
                reviewRequestDTO.getAuctionId(),
                reviewRequestDTO.getReview(),
                reviewRequestDTO.getRating(),
                reviewRequestDTO.getCreatedBy(),
                reviewRequestDTO.getUpdatedBy()
        );


        Review revObj = reviewRepository.save(revEntity);
        log.info("rev obj with id # {}", revObj.getId());

        ReviewResponseDTO responseDTO = new ReviewResponseDTO(
                revObj.getId(),
                revObj.getBuyerId(),
                revObj.getSellerId(),
                revObj.getAuctionId(),
                revObj.getReview(),
                revObj.getRating(),
                revObj.getVersion(),
                revObj.getCreatedAt(),
                revObj.getUpdatedAt(),
                revObj.getCreatedBy(),
                revObj.getUpdatedBy()
        );
        return responseDTO;
    }

    public List<ReviewResponseDTO> getMyBuyerReviews(Integer sellerId) {
        List<Review> reviews = reviewRepository.findBuyerReviewsForSeller(sellerId);
        List<ReviewResponseDTO> reviewDTO =reviews.stream()
                .map(e-> new ReviewResponseDTO(e.getId(),
                        e.getBuyerId(), e.getSellerId(),
                        e.getAuctionId(), e.getReview(), e.getRating(),
                        e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy()))
                        .collect(Collectors.toList());

        return reviewDTO;
    }

    @Override
    public List<ReviewResponseDTO> getMySellerReviews(Integer buyerId) {
        List<Review> reviews = reviewRepository.findSellerReviewsForBuyer(buyerId);
        List<ReviewResponseDTO> reviewDTO =reviews.stream()
                .map(e-> new ReviewResponseDTO(e.getId(),
                        e.getBuyerId(), e.getSellerId(),
                        e.getAuctionId(), e.getReview(), e.getRating(),
                        e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy()))
                .collect(Collectors.toList());

        return reviewDTO;
    }

    @Override
    public ReviewResponseDTO getReviewById(Integer id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if(optionalReview.isPresent()){
            return new ReviewResponseDTO(
                    optionalReview.get().getId(),
                    optionalReview.get().getBuyerId(),
                    optionalReview.get().getSellerId(),
                    optionalReview.get().getAuctionId(),
                    optionalReview.get().getReview(),
                    optionalReview.get().getRating(),
                    optionalReview.get().getCreatedAt(),
                    optionalReview.get().getUpdatedAt(),
                    optionalReview.get().getCreatedBy(),
                    optionalReview.get().getUpdatedBy()
                    );
        }
        return null;
    }

    @Override
    public ReviewResponseDTO updateReview(Integer id, ReviewRequestDTO updateReviewRequestDTO) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        log.info("{}", optionalReview);
        if (optionalReview.isEmpty()) {
            throw new RuntimeException("Review not found with id: " + id);
        }
        Review existingReview = optionalReview.get();
        Review updatedReview = new Review(
                existingReview.getId(),
                existingReview.getBuyerId(),
                existingReview.getSellerId(),
                existingReview.getAuctionId(),
                updateReviewRequestDTO.getReview() != null ? updateReviewRequestDTO.getReview() : existingReview.getReview(),
                updateReviewRequestDTO.getRating() != null ? updateReviewRequestDTO.getRating() : existingReview.getRating(),
                existingReview.getCreatedBy(),
                updateReviewRequestDTO.getUpdatedBy() != null ? updateReviewRequestDTO.getUpdatedBy() : existingReview.getUpdatedBy()
        );

        Review savedReview = reviewRepository.save(updatedReview);
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(
                savedReview.getId(),
                savedReview.getBuyerId(),
                savedReview.getSellerId(),
                savedReview.getAuctionId(),
                savedReview.getReview(),
                savedReview.getRating(),
                savedReview.getCreatedAt(),
                savedReview.getUpdatedAt(),
                savedReview.getCreatedBy(),
                savedReview.getUpdatedBy()
        );
        return responseDTO;
    }

    @Override
    public boolean deleteById(Integer id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            throw new RuntimeException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
        return true;
    }

    @Override
    public List<ReviewResponseDTO> findAll() {
        List<Review> allReviews = reviewRepository.findAll();
        log.info("{}",allReviews);
        List<ReviewResponseDTO> reviewDTO =allReviews.stream()
                                    .map(e-> new ReviewResponseDTO(e.getId(),
                                    e.getBuyerId(), e.getSellerId(),
                                    e.getAuctionId(), e.getReview(), e.getRating(),
                                    e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy()))
                                    .collect(Collectors.toList());
        return reviewDTO;
    }
}
