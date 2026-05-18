package com.realestate.controller;

import com.realestate.model.*;
import com.realestate.service.AgentService;
import com.realestate.service.AppointmentService;
import com.realestate.service.ReviewService;
import com.realestate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AgentService agentService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;

    // ─── DASHBOARD ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers",        userService.countByRole(Role.USER));
        model.addAttribute("totalAgents",       agentService.countAll());
        model.addAttribute("totalAppointments", appointmentService.countAll());
        model.addAttribute("pendingCount",      appointmentService.countByStatus(AppointmentStatus.PENDING));
        model.addAttribute("confirmedCount",    appointmentService.countByStatus(AppointmentStatus.CONFIRMED));
        model.addAttribute("completedCount",    appointmentService.countByStatus(AppointmentStatus.COMPLETED));

        List<Review> allReviews = reviewService.getAllReviews();
        model.addAttribute("totalReviews", allReviews.size());
        model.addAttribute("recentReviews", allReviews.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5).toList());

        model.addAttribute("recentAppointments",
                appointmentService.getAllAppointments().stream()
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .limit(5).toList());
        return "admin/dashboard";
    }

    // ─── USER CRUD ────────────────────────────────────────────────────────────
    @GetMapping("/users")
    public String listUsers(@RequestParam(required = false) String search, Model model) {
        List<User> users = (search != null && !search.isBlank())
                ? userService.searchUsers(search)
                : userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("searchKeyword", search);
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                           BindingResult result, Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/user-form";
        }
        try {
            if (user.getId() == null) {
                userService.registerUser(user);
                redirectAttributes.addFlashAttribute("successMsg", "User created successfully.");
            } else {
                userService.updateUser(user.getId(), user);
                redirectAttributes.addFlashAttribute("successMsg", "User updated successfully.");
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "admin/user-form";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMsg", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    // ─── AGENT CRUD ───────────────────────────────────────────────────────────
    @GetMapping("/agents")
    public String listAgents(@RequestParam(required = false) String search, Model model) {
        List<Agent> agents = (search != null && !search.isBlank())
                ? agentService.searchAgents(search)
                : agentService.getAllAgents();
        model.addAttribute("agents", agents);
        model.addAttribute("searchKeyword", search);
        return "admin/agents";
    }

    @GetMapping("/agents/new")
    public String newAgentForm(Model model) {
        model.addAttribute("agent", new Agent());
        return "admin/agent-form";
    }

    @PostMapping("/agents/save")
    public String saveAgent(@Valid @ModelAttribute("agent") Agent agent,
                            BindingResult result, Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/agent-form";
        }
        try {
            if (agent.getId() == null) {
                agentService.saveAgent(agent);
                redirectAttributes.addFlashAttribute("successMsg", "Agent created successfully.");
            } else {
                agentService.updateAgent(agent.getId(), agent);
                redirectAttributes.addFlashAttribute("successMsg", "Agent updated successfully.");
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "admin/agent-form";
        }
        return "redirect:/admin/agents";
    }

    @GetMapping("/agents/edit/{id}")
    public String editAgent(@PathVariable Long id, Model model) {
        Agent agent = agentService.getAgentById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        model.addAttribute("agent", agent);
        return "admin/agent-form";
    }

    @PostMapping("/agents/delete/{id}")
    public String deleteAgent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        agentService.deleteAgent(id);
        redirectAttributes.addFlashAttribute("successMsg", "Agent deleted successfully.");
        return "redirect:/admin/agents";
    }

    // ─── APPOINTMENT CRUD ─────────────────────────────────────────────────────
    @GetMapping("/appointments")
    public String listAppointments(@RequestParam(required = false) String status, Model model) {
        List<Appointment> appointments;
        if (status != null && !status.isBlank()) {
            appointments = appointmentService.getAppointmentsByStatus(AppointmentStatus.valueOf(status));
            model.addAttribute("selectedStatus", status);
        } else {
            appointments = appointmentService.getAllAppointments();
        }
        model.addAttribute("appointments", appointments);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "admin/appointments";
    }

    @GetMapping("/appointments/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("users",  userService.getUsersByRole(Role.USER));
        model.addAttribute("agents", agentService.getAllAgents());
        model.addAttribute("statuses", AppointmentStatus.values());
        model.addAttribute("propertyTypes", new String[]{"House","Apartment","Condo","Villa","Commercial","Land","Other"});
        model.addAttribute("budgetRanges",  new String[]{"Under $100K","$100K-$300K","$300K-$500K","$500K-$1M","Above $1M"});
        return "admin/appointment-form";
    }

    @PostMapping("/appointments/save")
    public String saveAppointment(@ModelAttribute Appointment appointment,
                                  @RequestParam Long userId,
                                  @RequestParam Long agentId,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        try {
            User user   = userService.getUserById(userId).orElseThrow();
            Agent agent = agentService.getAgentById(agentId).orElseThrow();
            appointment.setUser(user);
            appointment.setAgent(agent);
            if (appointment.getStatus() == null) appointment.setStatus(AppointmentStatus.PENDING);
            appointmentService.saveAppointment(appointment);
            redirectAttributes.addFlashAttribute("successMsg", "Appointment saved successfully.");
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("users",  userService.getUsersByRole(Role.USER));
            model.addAttribute("agents", agentService.getAllAgents());
            model.addAttribute("statuses", AppointmentStatus.values());
            return "admin/appointment-form";
        }
        return "redirect:/admin/appointments";
    }

    @GetMapping("/appointments/edit/{id}")
    public String editAppointment(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        model.addAttribute("appointment", appointment);
        model.addAttribute("users",  userService.getUsersByRole(Role.USER));
        model.addAttribute("agents", agentService.getAllAgents());
        model.addAttribute("statuses", AppointmentStatus.values());
        model.addAttribute("propertyTypes", new String[]{"House","Apartment","Condo","Villa","Commercial","Land","Other"});
        model.addAttribute("budgetRanges",  new String[]{"Under $100K","$100K-$300K","$300K-$500K","$500K-$1M","Above $1M"});
        return "admin/appointment-form";
    }

    @PostMapping("/appointments/update/{id}")
    public String updateAppointment(@PathVariable Long id,
                                    @ModelAttribute Appointment updated,
                                    @RequestParam Long userId,
                                    @RequestParam Long agentId,
                                    RedirectAttributes redirectAttributes) {
        User user   = userService.getUserById(userId).orElseThrow();
        Agent agent = agentService.getAgentById(agentId).orElseThrow();
        updated.setUser(user);
        updated.setAgent(agent);
        appointmentService.updateAppointment(id, updated);
        redirectAttributes.addFlashAttribute("successMsg", "Appointment updated successfully.");
        return "redirect:/admin/appointments";
    }

    @PostMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.deleteAppointment(id);
        redirectAttributes.addFlashAttribute("successMsg", "Appointment deleted successfully.");
        return "redirect:/admin/appointments";
    }

    @PostMapping("/appointments/status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam AppointmentStatus status,
                               RedirectAttributes redirectAttributes) {
        appointmentService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("successMsg", "Status updated to " + status + ".");
        return "redirect:/admin/appointments";
    }

    // ─── REVIEW MANAGEMENT ───────────────────────────────────────────────────
    @GetMapping("/reviews")
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews";
    }

    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.deleteReview(id);
        redirectAttributes.addFlashAttribute("successMsg", "Review deleted successfully.");
        return "redirect:/admin/reviews";
    }
}
