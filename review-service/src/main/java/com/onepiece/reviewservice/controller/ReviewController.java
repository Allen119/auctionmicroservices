package com.onepiece.reviewservice.controller;

import com.onepiece.reviewservice.dto.ReviewRequestDTO;
import com.onepiece.reviewservice.dto.ReviewResponseDTO;
import com.onepiece.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/review-service")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO){
        log.info("The auction id is # {}", reviewRequestDTO.getAuctionId());
        return new ResponseEntity<>(reviewService.createReview(reviewRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews(){
        return new ResponseEntity<>(reviewService.findAll(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public  ResponseEntity<?> getReview(@PathVariable Integer id){
        ReviewResponseDTO respDTO = reviewService.getReviewById(id);
        if(respDTO!=null) return new ResponseEntity<>(respDTO,HttpStatus.OK);
        return new ResponseEntity<>("No review found", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("seller/{id}")
    public ResponseEntity<List<ReviewResponseDTO>> getBySellerId(@PathVariable Integer id){
        List<ReviewResponseDTO> reviewDTO = reviewService.getMyBuyerReviews(id);
        return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    }

    @GetMapping("buyer/{id}")
    public ResponseEntity<List<ReviewResponseDTO>> getByBuyerId(@PathVariable Integer id){
        List<ReviewResponseDTO> reviewDTO = reviewService.getMySellerReviews(id);
        return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public  ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Integer id, @RequestBody ReviewRequestDTO updateReviewRequestDTO){
        ReviewResponseDTO respDTO = reviewService.updateReview(id, updateReviewRequestDTO);
        log.info("{}",respDTO);
        return new ResponseEntity<>(respDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deleteReview(@PathVariable Integer id){
        boolean isDeleted = reviewService.deleteById(id);
        if (isDeleted){
            log.info("Able to delete the review # {}", id);
            return new ResponseEntity("Review deleted successfully", HttpStatus.OK);
        }
        else {
            log.info("Unable to find the id # {}", id);
            return new ResponseEntity<>("Review not found", HttpStatus.OK);
        }
    }
}
