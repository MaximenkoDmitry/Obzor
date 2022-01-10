package com.itransition.obzor.controller;

import com.itransition.obzor.entity.Overview;
import com.itransition.obzor.entity.TypeOfOverview;
import com.itransition.obzor.service.CustomUserDetailsService;
import com.itransition.obzor.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class HomeController {
	@Autowired
	private OverviewService overviewService;
	@Autowired
	private CustomUserDetailsService userService;

	@GetMapping("/")
	public String viewHomePage(Model model){
		model.addAttribute("homePage",true);
		model.addAttribute("type_book",TypeOfOverview.BOOKS);
		model.addAttribute("type_film",TypeOfOverview.FILMS);
		model.addAttribute("type_game",TypeOfOverview.GAMES);
		model.addAttribute("top_films",overviewService.getTopOverviews(TypeOfOverview.FILMS));
		model.addAttribute("top_books",overviewService.getTopOverviews(TypeOfOverview.BOOKS));
		model.addAttribute("top_games",overviewService.getTopOverviews(TypeOfOverview.GAMES));
		return "home";
	}

	@GetMapping("/{type}")
	public String viewPageOfType(Model model,@PathVariable("type") TypeOfOverview type,
								 @RequestParam(value = "sort", required = false) String typeOfSort){
		model.addAttribute("books",TypeOfOverview.BOOKS);
		model.addAttribute("films",TypeOfOverview.FILMS);
		model.addAttribute("games",TypeOfOverview.GAMES);
		model.addAttribute("pageOfType",true);
		model.addAttribute("overviews", overviewService.sortOverviews(typeOfSort,type));
		return "home";
	}

	@GetMapping("/{type}/{overview}")
	public String viewOverview(@PathVariable("overview") Overview overview, Model model,
							   RedirectAttributes redirectAttributes,
							   @RequestHeader(required = false) String referer,
							   @RequestParam(value = "rating", required = false) Long rating){
		model.addAttribute("viewOverview", overviewService.getOverview(overview));
		model.addAttribute("isOverview",true);
		model.addAttribute("meRated", overview.getRatings().contains(userService.getAuthUser()));
		if(rating!=null){
			return "redirect:" + overviewService.rateOverview(overview,redirectAttributes,referer,rating);
		}
		return "home";
	}

	@GetMapping("/{type}/{overview}/like")
	public String likeOverview( @PathVariable("overview") Overview overview, RedirectAttributes redirectAttributes,
								@RequestHeader(required = false) String referer) {
		return "redirect:" + overviewService.likeOverview(overview,redirectAttributes,referer);
	}
	@GetMapping("/{type}/{overview}/rate")
	public String rateOverview( @PathVariable("overview") Overview overview, RedirectAttributes redirectAttributes,
								@RequestHeader(required = false) String referer,
								@RequestParam(value = "rating", required = false) Long rating) {
		System.out.println("RATING----------------" + rating);
		return "redirect:" + overviewService.rateOverview(overview,redirectAttributes,referer,rating);
	}

	@GetMapping("/add")
	public String addNewOverview(Model model){
		model.addAttribute("isCreateNewOverview",true);
		model.addAttribute("newOverview",new Overview());
		return "home";
	}

	@PostMapping("/add")
	public String saveNewOverview(Overview overview, @RequestParam Map<String, String> form, Model model){
		overviewService.saveOverview(overview,userService.getAuthUser(), form);
		return viewHomePage(model);
	}

	@GetMapping("/profile")
	public String viewProfile(Model model,@RequestParam(required = false, value = "keyword_profile") String keyword){
		model.addAttribute("profile",true);
		model.addAttribute("user",userService.getAuthUser());
		model.addAttribute("myOverview",overviewService.findByString(
				overviewService.getOverviewsOfUserId(userService.getAuthUser()),keyword));
		model.addAttribute("userLikes",userService.getLikesOfUser(userService.getAuthUser()));
		model.addAttribute("keyword_profile",keyword);
		return "home";
	}

	@GetMapping("/profile/{overview}/edit")
	public String editOverview(@PathVariable("overview") Overview overview, Model model){
		model.addAttribute("editOverview",overview);
		model.addAttribute("emptyOver",new Overview());
		model.addAttribute("edit",true);
		return "home";
	}

	@PostMapping("/profile/{overview}/edit")
	public String uploadOverview( @PathVariable("overview") Overview overview, @RequestParam String title,
									@RequestParam String description, Model model){
		overviewService.editOverview(overview,title,description);
		return viewHomePage(model);
	}

	@GetMapping("/profile/{overview}/remove")
	public String removeOverview( @PathVariable("overview") Overview overview, Model model){
		overviewService.deleteOverview(overview.getId());
		return viewHomePage(model);
	}

	@GetMapping("/search")
	public String searchOverview(@Param("keyword") String keyword, Model model){
		model.addAttribute("search", true);
		model.addAttribute("findOverviews",overviewService.searchPreview(keyword));
		return "home";
	}
}
