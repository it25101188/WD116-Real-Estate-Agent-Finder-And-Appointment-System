package com.realestate.service;

import com.realestate.model.Agent;
import com.realestate.model.Review;
import com.realestate.repository.AgentRepository;
import com.realestate.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AgentRepository agentRepository;

    public List<Review> getReviewsByAgentId(Long agentId) {
        return reviewRepository.findByAgentIdOrderByCreatedAtDesc(agentId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public Review saveReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        updateAgentRating(review.getAgent().getId());
        return savedReview;
    }

    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Long agentId = review.getAgent().getId();
        reviewRepository.deleteById(id);
        updateAgentRating(agentId);
    }

    private void updateAgentRating(Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        
        List<Review> reviews = reviewRepository.findByAgentIdOrderByCreatedAtDesc(agentId);
        
        if (reviews.isEmpty()) {
            agent.setRating(0.0);
            agent.setTotalReviews(0);
        } else {
            double sum = reviews.stream().mapToInt(Review::getRating).sum();
            double average = sum / reviews.size();
            agent.setRating(average);
            agent.setTotalReviews(reviews.size());
        }
        
        agentRepository.save(agent);
    }
}
