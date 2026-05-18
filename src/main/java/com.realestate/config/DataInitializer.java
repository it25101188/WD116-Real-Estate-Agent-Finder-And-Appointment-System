package com.realestate.config;

import com.realestate.model.Agent;
import com.realestate.model.Role;
import com.realestate.model.User;
import com.realestate.repository.AgentRepository;
import com.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create Admin
        try {
            if (!userRepository.existsByEmail("admin@realestate.com")) {
                User admin = new User();
                admin.setFirstName("Super");
                admin.setLastName("Admin");
                admin.setEmail("admin@realestate.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setPhoneNumber("0000000000");
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);
                log.info("Admin user created: admin@realestate.com / admin123");
            }
        } catch (Exception e) {
            log.error("Failed to create admin user: " + e.getMessage());
        }

        // Create a sample Agent user
        if (!userRepository.existsByEmail("agent@realestate.com")) {
            User agentUser = new User();
            agentUser.setFirstName("John");
            agentUser.setLastName("Smith");
            agentUser.setEmail("agent@realestate.com");
            agentUser.setPassword(passwordEncoder.encode("agent123"));
            agentUser.setPhoneNumber("9876543210");
            agentUser.setRole(Role.AGENT);
            agentUser.setEnabled(true);
            userRepository.save(agentUser);
            log.info("Agent user created: agent@realestate.com / agent123");
        }

        // Create a sample regular User
        if (!userRepository.existsByEmail("user@realestate.com")) {
            User regularUser = new User();
            regularUser.setFirstName("Jane");
            regularUser.setLastName("Doe");
            regularUser.setEmail("user@realestate.com");
            regularUser.setPassword(passwordEncoder.encode("user123"));
            regularUser.setPhoneNumber("1234567890");
            regularUser.setRole(Role.USER);
            regularUser.setEnabled(true);
            userRepository.save(regularUser);
            log.info("Regular user created: user@realestate.com / user123");
        }

        // Create sample Agents
        if (agentRepository.count() == 0) {
            String[][] agentsData = {
                    {"John", "Smith", "agent@realestate.com", "9876543210", "Residential Sales",
                            "REA-001", "Expert in luxury residential properties with 10+ years experience.", "10", "Downtown, New York"},
                    {"Sarah", "Johnson", "sarah.j@realestate.com", "8765432109", "Commercial Real Estate",
                            "REA-002", "Specializing in commercial properties and office spaces.", "8", "Manhattan, New York"},
                    {"Michael", "Chen", "m.chen@realestate.com", "7654321098", "Property Investment",
                            "REA-003", "Investment property expert helping clients maximize ROI.", "12", "Brooklyn, New York"},
                    {"Emily", "Williams", "emily.w@realestate.com", "6543210987", "Luxury Apartments",
                            "REA-004", "Dedicated to finding dream luxury apartments for discerning clients.", "6", "Upper East Side, NY"},
                    {"Robert", "Brown", "r.brown@realestate.com", "5432109876", "Suburban Homes",
                            "REA-005", "Helping families find their perfect suburban home since 2012.", "14", "Queens, New York"},
                    {"Lisa", "Davis", "l.davis@realestate.com", "4321098765", "Rental Properties",
                            "REA-006", "Rental specialist connecting tenants with ideal properties.", "5", "Jersey City, NJ"}
            };

            double[] ratings = {4.8, 4.6, 4.9, 4.7, 4.5, 4.4};
            int[] reviews = {124, 98, 156, 87, 203, 65};

            for (int i = 0; i < agentsData.length; i++) {
                String[] d = agentsData[i];
                Agent agent = new Agent();
                agent.setFirstName(d[0]);
                agent.setLastName(d[1]);
                agent.setEmail(d[2]);
                agent.setPhoneNumber(d[3]);
                agent.setSpecialization(d[4]);
                agent.setLicenseNumber(d[5]);
                agent.setBio(d[6]);
                agent.setYearsExperience(Integer.parseInt(d[7]));
                agent.setOfficeAddress(d[8]);
                agent.setRating(ratings[i]);
                agent.setTotalReviews(reviews[i]);
                agent.setAvailable(true);
                agentRepository.save(agent);
            }
            log.info("Sample agents created successfully.");
        }
    }
}
