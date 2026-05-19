package com.realestate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Specialization is required")
    @Column(nullable = false)
    private String specialization;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "office_address")
    private String officeAddress;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
