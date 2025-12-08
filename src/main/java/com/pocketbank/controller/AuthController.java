package com.pocketbank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pocketbank.config.service.UserService;
import com.pocketbank.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	@GetMapping("/login")
	public String loginPage(@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout,
			Model model) {

		if (error != null) {
			model.addAttribute("error", "Invalid username or password");
		}
		if (logout != null) {
			model.addAttribute("message", "Logged out successfully");
		}
		return "login";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute User user,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "register";
		}

		try {
			userService.registerUser(user);
			redirectAttributes.addFlashAttribute("success", "Registeratio successful! please login.");
			return "redirect:/login";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/register";
		}
	}
}
