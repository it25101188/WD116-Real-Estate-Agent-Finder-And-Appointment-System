package com.realestate.controller;

import com.realestate.model.*;
import com.realestate.service.AgentService;
import com.realestate.service.AppointmentService;
import com.realestate.service.ReviewService;
import com.realestate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

	private final AgentService agentService;
	private final AppointmentService appointmentService;
	private final UserService userService;
	private final ReviewService reviewService;

	private User getCurrentUser(Authentication auth) {
		return userService.getUserByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model, Authentication auth) {
		User user = getCurrentUser(auth);
		// Find agent profile linked by email
		Agent agent = agentService.getAgentByEmail(user.getEmail()).orElse(null);

		if (agent != null) {
			List<Appointment> appointments = appointmentService.getAppointmentsByAgentId(agent.getId());
			long pending   = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
			long confirmed = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED).count();
			long completed = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
			long cancelled = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count();

			model.addAttribute("appointments", appointments);
			model.addAttribute("pendingCount",   pending);
			model.addAttribute("confirmedCount", confirmed);
			model.addAttribute("completedCount", completed);
			model.addAttribute("cancelledCount", cancelled);
			model.addAttribute("totalAppointments", appointments.size());
			model.addAttribute("agent", agent);
			model.addAttribute("reviews", reviewService.getReviewsByAgentId(agent.getId()));
		}

		model.addAttribute("user", user);
		return "agent/dashboard";
	}

	@GetMapping("/appointments")
	public String myAppointments(Model model, Authentication auth) {
		User user = getCurrentUser(auth);
		Agent agent = agentService.getAgentByEmail(user.getEmail()).orElse(null);
		if (agent != null) {
			List<Appointment> appointments = appointmentService.getAppointmentsByAgentId(agent.getId());
			model.addAttribute("appointments", appointments);
			model.addAttribute("agent", agent);
		}
		model.addAttribute("user", user);
		model.addAttribute("statuses", AppointmentStatus.values());
		return "agent/appointments";
	}

	@PostMapping("/appointments/update-status/{id}")
	public String updateStatus(@PathVariable Long id,
							   @RequestParam AppointmentStatus status,
							   RedirectAttributes redirectAttributes) {
		appointmentService.updateStatus(id, status);
		redirectAttributes.addFlashAttribute("successMsg", "Appointment status updated to " + status + ".");
		return "redirect:/agent/appointments";
	}

	@GetMapping("/appointments/edit/{id}")
	public String editAppointment(@PathVariable Long id, Model model) {
		Appointment appointment = appointmentService.getAppointmentById(id)
				.orElseThrow(() -> new RuntimeException("Appointment not found"));
		model.addAttribute("appointment", appointment);
		model.addAttribute("statuses", AppointmentStatus.values());
		return "agent/edit-appointment";
	}

	@PostMapping("/appointments/update/{id}")
	public String updateAppointment(@PathVariable Long id,
									@ModelAttribute Appointment updated,
									RedirectAttributes redirectAttributes) {
		appointmentService.updateAppointment(id, updated);
		redirectAttributes.addFlashAttribute("successMsg", "Appointment updated successfully.");
		return "redirect:/agent/appointments";
	}

	@PostMapping("/appointments/delete/{id}")
	public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		appointmentService.deleteAppointment(id);
		redirectAttributes.addFlashAttribute("successMsg", "Appointment deleted.");
		return "redirect:/agent/appointments";
	}

	@GetMapping("/profile")
	public String profile(Model model, Authentication auth) {
		User user = getCurrentUser(auth);
		Agent agent = agentService.getAgentByEmail(user.getEmail()).orElse(null);
		model.addAttribute("user", user);
		model.addAttribute("agent", agent);
		if (agent != null) {
			model.addAttribute("reviews", reviewService.getReviewsByAgentId(agent.getId()));
		}
		return "agent/profile";
	}

	@PostMapping("/profile/update")
	public String updateProfile(@ModelAttribute Agent updatedAgent,
								Authentication auth,
								RedirectAttributes redirectAttributes) {
		User user = getCurrentUser(auth);
		Agent existing = agentService.getAgentByEmail(user.getEmail()).orElse(null);
		if (existing != null) {
			try {
				agentService.updateAgent(existing.getId(), updatedAgent);
				redirectAttributes.addFlashAttribute("successMsg", "Profile updated successfully.");
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("errorMsg", "Update failed: " + e.getMessage());
			}
		}
		return "redirect:/agent/profile";
	}
}

