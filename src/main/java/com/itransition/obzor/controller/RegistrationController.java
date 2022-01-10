package com.itransition.obzor.controller;

import com.itransition.obzor.entity.User;
import com.itransition.obzor.repo.UserRepository;
import com.itransition.obzor.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
	@Autowired
	private CustomUserDetailsService userService;

	@GetMapping("/registration")
	public String viewRegistrationPage(Model model){
		model.addAttribute("user", new User());
		return "registration";
	}

	@PostMapping("/registration")
	public String registration(User user,Model model){
		model.addAttribute("usernameError", "User with this username is created!");
		if (!userService.saveUser(user)){
			return "registration";
		}
		return "login";
	}
}
