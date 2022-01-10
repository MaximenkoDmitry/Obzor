package com.itransition.obzor.controller;

import com.itransition.obzor.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {
	private static final String authorizationRequestBaseUri = "oauth2/authorization";

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@GetMapping("/login")
	@SuppressWarnings("unchecked")
	public String getLoginPage(Model model, @RequestParam(required = false, value = "error") String error ) {
		model.addAttribute("error", error);

		Iterable<ClientRegistration> clientRegistrations;

		final Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

		final ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
				.as(Iterable.class);

		if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
			clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;

			clientRegistrations.forEach(registration ->
					oauth2AuthenticationUrls.put(registration.getClientName(),
							authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
		}

		model.addAttribute("urls", oauth2AuthenticationUrls);
		return "login";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		authentication.setAuthenticated(false);
		new SecurityContextLogoutHandler().logout(request, response, authentication);
		SecurityContextHolder.clearContext();
		request.logout();
		request.getSession().invalidate();
		return "redirect:/";
	}
}