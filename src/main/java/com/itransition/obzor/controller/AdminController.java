package com.itransition.obzor.controller;

import com.itransition.obzor.entity.Overview;
import com.itransition.obzor.entity.TypeOfOverview;
import com.itransition.obzor.entity.User;
import com.itransition.obzor.service.CustomUserDetailsService;
import com.itransition.obzor.service.MarkdownService;
import com.itransition.obzor.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AdminController {
	@Autowired
	private CustomUserDetailsService userService;

	@Autowired
	private OverviewService overviewService;

	@GetMapping("/admin")
	private String userList(Model model){
		model.addAttribute("users", userService.allUsers());
		return  "adminPage";
	}

	@GetMapping("/admin/{user}")
	public String profileUser( @PathVariable("user") User user, Model model,
							@RequestParam(required = false, value = "keyword_profile") String keyword){
		model.addAttribute("us", user);
		model.addAttribute("showProfile",true);
		model.addAttribute("myOverview",overviewService.findByString(
				overviewService.getOverviewsOfUserId(user),keyword));
		model.addAttribute("userLikes",userService.getLikesOfUser(user));
		model.addAttribute("keyword_profile",keyword);
		return "adminPage";
	}

	@GetMapping("/admin/{user}/add")
	public String addOverview( @PathVariable("user") User user, Model model){
		model.addAttribute("newOverview",new Overview());
		model.addAttribute("userCreator", user);
		model.addAttribute("createOverview",true);
		return "adminPage";
	}

	@PostMapping("/admin/{user}/add")
	public String save(@PathVariable("user") User user,Overview overview,
						 @RequestParam Map<String, String> form,
						 Model model){

		Set<String> types = Arrays.stream(TypeOfOverview.values()).map(TypeOfOverview::name).collect(Collectors.toSet());
		for(String key:form.keySet()){
			if(types.contains(key)){
				overview.setType(TypeOfOverview.valueOf(key));
				break;
			}
		}
		MarkdownService markdownService = new MarkdownService();
		overview.setHtml(markdownService.markdownToHTML(overview.getDescription()));
		overviewService.saveOverview(overview,user,form);
		model.addAttribute("users", userService.allUsers());
		return "adminPage";
	}

	@GetMapping("/admin/{user}/{overview}/view")
	public String viewOverview( @PathVariable("user") User user,@PathVariable("overview") Overview overview, Model model){
		model.addAttribute("viewOverview",overview);
		model.addAttribute("userCreator", user);
		model.addAttribute("view",true);
		return "adminPage";
	}

	@GetMapping("/admin/{user}/{overview}/edit")
	public String editOverview( @PathVariable("user") User user,@PathVariable("overview") Overview overview, Model model){
		model.addAttribute("editOverview",overview);
		model.addAttribute("emptyOver",new Overview());
		model.addAttribute("author", user);
		model.addAttribute("edit",true);
		return "adminPage";
	}

	@PostMapping("/admin/{user}/{overview}/edit")
	public String SaveEditOverview( @PathVariable("user") User user,
									@PathVariable("overview") Overview overview,
									@RequestParam String title,
									@RequestParam String description,
									Model model){
		overviewService.editOverview(overview,title,description);
		model.addAttribute("users", userService.allUsers());
		return "adminPage";
	}

	@GetMapping("/admin/{user}/{overview}/remove")
	public String removeOverview( @PathVariable("user") User user,@PathVariable("overview") Overview overview, Model model){
		overviewService.deleteOverview(overview.getId());
		model.addAttribute("users", userService.allUsers());
		return "adminPage";
	}
}
