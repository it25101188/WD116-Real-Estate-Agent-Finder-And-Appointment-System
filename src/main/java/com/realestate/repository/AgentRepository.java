package com.realestate.repository;

import com.realestate.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Agent> findByAvailableTrue();
    List<Agent> findBySpecializationContainingIgnoreCase(String specialization);

    @Query("SELECT a FROM Agent a WHERE " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.specialization) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.officeAddress) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Agent> searchAgents(@Param("keyword") String keyword);

    List<Agent> findByOrderByRatingDesc();
    long count();
}
