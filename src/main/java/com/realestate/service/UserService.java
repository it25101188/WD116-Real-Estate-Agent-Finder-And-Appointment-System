package com.realestate.service;

import com.realestate.model.Role;
import com.realestate.model.User;
import com.realestate.model.Agent;
import com.realestate.repository.UserRepository;
import com.realestate.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        user.setEnabled(true);
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.AGENT && !agentRepository.existsByEmail(savedUser.getEmail())) {
            Agent agent = new Agent();
            agent.setFirstName(savedUser.getFirstName());
            agent.setLastName(savedUser.getLastName());
            agent.setEmail(savedUser.getEmail());
            agent.setPhoneNumber(savedUser.getPhoneNumber());
            agent.setAvailable(true);
            agentRepository.save(agent);
        }

        return savedUser;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setPhoneNumber(updatedUser.getPhoneNumber());
        existing.setRole(updatedUser.getRole());
        existing.setEnabled(updatedUser.isEnabled());
        // Only update email if changed and not taken
        if (!existing.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Email already in use: " + updatedUser.getEmail());
            }
            existing.setEmail(updatedUser.getEmail());
        }
        // Only update password if a new one is provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }

    public long countAll() {
        return userRepository.count();
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
