package com.realestate.controller;

import com.realestate.model.Role;
import com.realestate.model.User;
import com.realestate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "You have been successfully logged out.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "register";
        }
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("emailError", "This email is already registered.");
            model.addAttribute("roles", Role.values());
            return "register";
        }
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Registration successful! Please login with your credentials.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Registration failed: " + e.getMessage());
            model.addAttribute("roles", Role.values());
            return "register";
        }
    }
}
