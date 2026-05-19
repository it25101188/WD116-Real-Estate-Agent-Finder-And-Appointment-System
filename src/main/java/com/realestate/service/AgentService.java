package com.realestate.service;

import com.realestate.model.Agent;
import com.realestate.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AgentService {

    private final AgentRepository agentRepository;

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public List<Agent> getAvailableAgents() {
        return agentRepository.findByAvailableTrue();
    }

    public Optional<Agent> getAgentById(Long id) {
        return agentRepository.findById(id);
    }

    public Optional<Agent> getAgentByEmail(String email) {
        return agentRepository.findByEmail(email);
    }

    public Agent saveAgent(Agent agent) {
        if (agent.getId() == null && agentRepository.existsByEmail(agent.getEmail())) {
            throw new RuntimeException("Email already registered: " + agent.getEmail());
        }
        return agentRepository.save(agent);
    }

    public Agent updateAgent(Long id, Agent updatedAgent) {
        Agent existing = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found with id: " + id));
        existing.setFirstName(updatedAgent.getFirstName());
        existing.setLastName(updatedAgent.getLastName());
        existing.setPhoneNumber(updatedAgent.getPhoneNumber());
        existing.setSpecialization(updatedAgent.getSpecialization());
        existing.setLicenseNumber(updatedAgent.getLicenseNumber());
        existing.setBio(updatedAgent.getBio());
        existing.setYearsExperience(updatedAgent.getYearsExperience());
        existing.setOfficeAddress(updatedAgent.getOfficeAddress());
        existing.setAvailable(updatedAgent.isAvailable());
        existing.setRating(updatedAgent.getRating());
        if (!existing.getEmail().equals(updatedAgent.getEmail())) {
            if (agentRepository.existsByEmail(updatedAgent.getEmail())) {
                throw new RuntimeException("Email already in use: " + updatedAgent.getEmail());
            }
            existing.setEmail(updatedAgent.getEmail());
        }
        return agentRepository.save(existing);
    }

    public void deleteAgent(Long id) {
        agentRepository.deleteById(id);
    }

    public List<Agent> searchAgents(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return agentRepository.findAll();
        }
        return agentRepository.searchAgents(keyword);
    }

    public List<Agent> getTopRatedAgents() {
        return agentRepository.findByOrderByRatingDesc();
    }

    public long countAll() {
        return agentRepository.count();
    }

    public boolean emailExists(String email) {
        return agentRepository.existsByEmail(email);
    }
}
