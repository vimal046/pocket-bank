package com.pocketbank.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.pocketbank.config.service.UserService;
import com.pocketbank.entity.enums.Role;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashBoardController {

	private final UserService userService;

	@GetMapping("/dashboard")
	public String dashboard(Authentication auth) {
		var user = userService.findByUsername(auth.getName());
		if (user.getRole() == Role.ADMIN) {
			return "redirect:/admin/dashboard";
		}
		return "redirect:/customer/dashboard";
	}
}
