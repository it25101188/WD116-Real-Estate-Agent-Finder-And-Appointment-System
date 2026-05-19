package com.realestate.controller;

import com.realestate.model.Agent;
import com.realestate.model.Review;
import com.realestate.model.User;
import com.realestate.service.AgentService;
import com.realestate.service.ReviewService;
import com.realestate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final AgentService agentService;

    @PostMapping("/add")
    public String addReview(@RequestParam Long agentId,
                            @RequestParam Integer rating,
                            @RequestParam String comment,
                            Authentication auth,
                            RedirectAttributes redirectAttributes) {
        
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Agent agent = agentService.getAgentById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        Review review = new Review();
        review.setUser(user);
        review.setAgent(agent);
        review.setRating(rating);
        review.setComment(comment);

        reviewService.saveReview(review);

        redirectAttributes.addFlashAttribute("successMsg", "Thank you for your review!");
        return "redirect:/user/book/" + agentId;
    }
}
