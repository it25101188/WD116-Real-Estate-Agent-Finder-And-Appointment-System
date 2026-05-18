package com.realestate.controller;

import com.realestate.model.*;
import com.realestate.service.AgentService;
import com.realestate.service.AppointmentService;
import com.realestate.service.ReviewService;
import com.realestate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AgentService agentService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;

    private User getCurrentUser(Authentication auth) {
        return userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        List<Appointment> appointments = appointmentService.getAppointmentsByUserId(user.getId());
        long pending = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        long confirmed = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED).count();
        long completed = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();

        model.addAttribute("user", user);
        model.addAttribute("appointments", appointments);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("confirmedCount", confirmed);
        model.addAttribute("completedCount", completed);
        model.addAttribute("totalAppointments", appointments.size());
        return "user/dashboard";
    }

    @GetMapping("/appointments")
    public String myAppointments(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        List<Appointment> appointments = appointmentService.getAppointmentsByUserId(user.getId());
        model.addAttribute("appointments", appointments);
        model.addAttribute("user", user);
        return "user/appointments";
    }

    @GetMapping("/book/{agentId}")
    public String bookForm(@PathVariable Long agentId, Model model, Authentication auth) {
        Agent agent = agentService.getAgentById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        User user = getCurrentUser(auth);
        Appointment appointment = new Appointment();
        appointment.setAgent(agent);
        appointment.setUser(user);
        model.addAttribute("appointment", appointment);
        model.addAttribute("agent", agent);
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviewService.getReviewsByAgentId(agentId));
        model.addAttribute("propertyTypes", new String[]{"House", "Apartment", "Condo", "Villa", "Commercial", "Land", "Other"});
        model.addAttribute("budgetRanges", new String[]{"Under $100K", "$100K-$300K", "$300K-$500K", "$500K-$1M", "Above $1M"});
        return "user/book-appointment";
    }

    @PostMapping("/book")
    public String bookAppointment(@Valid @ModelAttribute("appointment") Appointment appointment,
                                  BindingResult result,
                                  @RequestParam Long agentId,
                                  Authentication auth,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Agent agent = agentService.getAgentById(agentId).orElseThrow();
            model.addAttribute("agent", agent);
            model.addAttribute("propertyTypes", new String[]{"House", "Apartment", "Condo", "Villa", "Commercial", "Land", "Other"});
            model.addAttribute("budgetRanges", new String[]{"Under $100K", "$100K-$300K", "$300K-$500K", "$500K-$1M", "Above $1M"});
            return "user/book-appointment";
        }
        try {
            User user = getCurrentUser(auth);
            Agent agent = agentService.getAgentById(agentId).orElseThrow();
            appointment.setUser(user);
            appointment.setAgent(agent);
            appointment.setStatus(AppointmentStatus.PENDING);
            Appointment saved = appointmentService.saveAppointment(appointment);
            redirectAttributes.addFlashAttribute("appointmentId", saved.getId());
            redirectAttributes.addFlashAttribute("agentName", agent.getFullName());
            return "redirect:/user/appointment-success";
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Booking failed: " + e.getMessage());
            return "user/book-appointment";
        }
    }

    @GetMapping("/appointment-success")
    public String appointmentSuccess() {
        return "user/appointment-success";
    }

    @GetMapping("/appointments/edit/{id}")
    public String editAppointment(@PathVariable Long id, Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (!appointment.getUser().getId().equals(user.getId())) {
            return "redirect:/user/appointments?error=unauthorized";
        }
        model.addAttribute("appointment", appointment);
        model.addAttribute("propertyTypes", new String[]{"House", "Apartment", "Condo", "Villa", "Commercial", "Land", "Other"});
        model.addAttribute("budgetRanges", new String[]{"Under $100K", "$100K-$300K", "$300K-$500K", "$500K-$1M", "Above $1M"});
        model.addAttribute("statuses", AppointmentStatus.values());
        return "user/edit-appointment";
    }

    @PostMapping("/appointments/update/{id}")
    public String updateAppointment(@PathVariable Long id,
                                    @ModelAttribute Appointment updatedAppointment,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(auth);
        Appointment existing = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (!existing.getUser().getId().equals(user.getId())) {
            return "redirect:/user/appointments?error=unauthorized";
        }
        appointmentService.updateAppointment(id, updatedAppointment);
        redirectAttributes.addFlashAttribute("successMsg", "Appointment updated successfully.");
        return "redirect:/user/appointments";
    }

    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(auth);
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (!appointment.getUser().getId().equals(user.getId())) {
            return "redirect:/user/appointments?error=unauthorized";
        }
        appointmentService.updateStatus(id, AppointmentStatus.CANCELLED);
        redirectAttributes.addFlashAttribute("successMsg", "Appointment cancelled.");
        return "redirect:/user/appointments";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, Authentication auth,
                                RedirectAttributes redirectAttributes) {
        User current = getCurrentUser(auth);
        updatedUser.setId(current.getId());
        updatedUser.setRole(current.getRole());
        try {
            userService.updateUser(current.getId(), updatedUser);
            redirectAttributes.addFlashAttribute("successMsg", "Profile updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Update failed: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }
}
