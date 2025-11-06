package com.onepiece.reviewservice.service;

import com.onepiece.reviewservice.dto.ReviewRequestDTO;
import com.onepiece.reviewservice.dto.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {

    ReviewResponseDTO createReview(ReviewRequestDTO reviewRequestDTO);

    List<ReviewResponseDTO> getMyBuyerReviews(Integer id);

    ReviewResponseDTO updateReview(Integer id, ReviewRequestDTO updateReviewRequestDTO);

    boolean deleteById(Integer id);

//    List<ReviewDTO> findAll();

    List<ReviewResponseDTO> getMySellerReviews(Integer id);

    ReviewResponseDTO getReviewById(Integer id);
}
