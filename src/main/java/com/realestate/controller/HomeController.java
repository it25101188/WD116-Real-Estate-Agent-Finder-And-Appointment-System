package com.realestate.controller;

import com.realestate.model.Agent;
import com.realestate.service.AgentService;
import com.realestate.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AgentService agentService;
    private final AppointmentService appointmentService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Agent> topAgents = agentService.getTopRatedAgents();
        // Show only top 3 on home page
        if (topAgents.size() > 3) {
            topAgents = topAgents.subList(0, 3);
        }
        model.addAttribute("topAgents", topAgents);
        model.addAttribute("totalAgents", agentService.countAll());
        model.addAttribute("totalAppointments", appointmentService.countAll());
        return "home";
    }

    @GetMapping("/agents")
    public String agents(Model model, @RequestParam(required = false) String search) {
        List<Agent> agents;
        if (search != null && !search.isBlank()) {
            agents = agentService.searchAgents(search);
            model.addAttribute("searchKeyword", search);
        } else {
            agents = agentService.getAllAgents();
        }
        model.addAttribute("agents", agents);
        return "agents";
    }

    @GetMapping("/agents/search")
    public String searchAgents(@RequestParam String keyword, Model model) {
        List<Agent> agents = agentService.searchAgents(keyword);
        model.addAttribute("agents", agents);
        model.addAttribute("searchKeyword", keyword);
        return "agents";
    }
}
