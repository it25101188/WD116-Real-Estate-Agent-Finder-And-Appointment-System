package com.realestate.repository;

import com.realestate.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAgentIdOrderByCreatedAtDesc(Long agentId);
}
