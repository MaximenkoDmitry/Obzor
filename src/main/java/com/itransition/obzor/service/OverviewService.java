package com.itransition.obzor.service;

import com.itransition.obzor.entity.Overview;
import com.itransition.obzor.entity.TypeOfOverview;
import com.itransition.obzor.entity.User;
import com.itransition.obzor.entity.dto.OverviewDTO;
import com.itransition.obzor.repo.OverviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OverviewService {
	@Autowired
	CustomUserDetailsService userService;

	private OverviewRepository overviewRepository;

	public MarkdownService markdownService = new MarkdownService();

	@Autowired
	public OverviewService( OverviewRepository overviewRepository) {
		this.overviewRepository = overviewRepository;
	}

	public List<OverviewDTO> getOverviewsOfType(TypeOfOverview typeOfOverview, User user){
		List<OverviewDTO> list=new ArrayList<>();
		for (OverviewDTO overview: overviewRepository.findAll(user)) {
			if(overview.getType().equals(typeOfOverview)){
				list.add(overview);
			}
		}
		return list;
	}

	public List<OverviewDTO> getPopularOverviewsAscending(TypeOfOverview type){
		List<OverviewDTO> overviews = new ArrayList<OverviewDTO>(getOverviewsOfType(type, userService.getAuthUser()));
		Collections.sort(overviews, OverviewDTO.COMPARE_BY_RATING);
		return overviews;
	}

	public List<OverviewDTO> getPopularOverviewsDescending(TypeOfOverview type){
		List<OverviewDTO> overviews = new ArrayList<>(getOverviewsOfType(type, userService.getAuthUser()));
		Collections.sort(overviews, OverviewDTO.COMPARE_BY_RATING.reversed());
		return overviews;
	}

	public List<OverviewDTO> getNewOverviews(TypeOfOverview type){
		List<OverviewDTO> overviews = new ArrayList<>(getOverviewsOfType(type, userService.getAuthUser()));
		Collections.reverse(overviews);
		return overviews;
	}
	public List<OverviewDTO> getOldOverviews(TypeOfOverview type){
		List<OverviewDTO> overviews = new ArrayList<>(getOverviewsOfType(type, userService.getAuthUser()));
		return overviews;
	}

	public List<OverviewDTO> sortOverviews(String typeOfSort, TypeOfOverview type){
		if(typeOfSort==null){
			return getPopularOverviewsDescending(type);
		}
		if(typeOfSort.equals("new")){
			return getNewOverviews(type);
		}else{
			if (typeOfSort.equals("old")) {
				return getOldOverviews(type);
			}
			else{
				if(typeOfSort.equals("rating_up")){
					return getPopularOverviewsAscending(type);
				}
			}
		}
		return getPopularOverviewsDescending(type);
	}

	public OverviewDTO getOverview(Overview overview){
		return overviewRepository.findById(overview.getId(),userService.getAuthUser());
	}
	public List<OverviewDTO> getOverviewsOfUserId(User user){
		List<OverviewDTO> list=new ArrayList<>();
		for (OverviewDTO overview: getAllOverviews(user)) {
			if(overview.getAuthor().equals(user)){
				list.add(overview);
			}
		}
		return list;
	}

	public List<OverviewDTO> getAllOverviews(User user) {
		return overviewRepository.findAll(user);
	}

	public boolean editOverview(Overview overview, String title, String description) {
		overview.setTitle(title);
		overview.setDescription(description);
		overview.setHtml(markdownService.markdownToHTML(overview.getDescription()));
		overviewRepository.save(overview);
		return true;
	}

	public boolean saveOverview(Overview overview, User author, Map<String, String> form) {
		Set<String> types = Arrays.stream(TypeOfOverview.values()).map(TypeOfOverview::name).collect(Collectors.toSet());
		for(String key:form.keySet()){
			if(types.contains(key)){
				overview.setType(TypeOfOverview.valueOf(key));
				break;
			}
		}
		overview.setHtml(markdownService.markdownToHTML(overview.getDescription()));
		overview.setAuthor(author);
		overview.setRating(0.0);
		overview.setDateCreated(new Date());
		overviewRepository.save(overview);
		return true;
	}

	public List<OverviewDTO> findByString(List<OverviewDTO> overviews, String keyword){
		if(keyword==null)
			return overviews;
		CharSequence charSequence = new StringBuffer(keyword);
		List <OverviewDTO> result = new ArrayList<OverviewDTO>();
		for (OverviewDTO over: overviews) {
			if(over.getDescription().contains(charSequence)||over.getTitle().contains(charSequence)){
				result.add(over);
			}
		}
		return result;
	}

	public void deleteOverview(Long id){
		overviewRepository.deleteById(id);
	}

	public List<Overview> searchPreview(String keyword){
		return overviewRepository.findAll(keyword);
	}

	public String likeOverview(Overview overview, RedirectAttributes redirectAttributes, String referer){
		User currentUser = userService.getAuthUser();
		Set<User> likes = overview.getLikes();

		if (likes.contains(currentUser)) {
			likes.remove(currentUser);
		} else {
			likes.add(currentUser);
		}
		overviewRepository.save(overview);

		UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();

		components.getQueryParams()
				.entrySet()
				.forEach(pair -> redirectAttributes.addAttribute(pair.getKey(), pair.getValue()));
		return components.getPath();
	}

	public List<OverviewDTO> getTopOverviews(TypeOfOverview type){
		List<OverviewDTO> topOverviews = getPopularOverviewsDescending(type);
		if(topOverviews.size()>6){
			return topOverviews.subList(0,6);
		}
		return topOverviews;
	}

	public String rateOverview(Overview overview, RedirectAttributes redirectAttributes, String referer, Long rating){
		User currentUser = userService.getAuthUser();
		Double newRating = overview.getRating();
		Set<User> ratings = overview.getRatings();

		if (!ratings.contains(currentUser)) {
			overview.setRating(new BigDecimal(((newRating * overview.getRatings().size())+rating)/(overview.getRatings().size()+1)).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
			ratings.add(currentUser);
		}
		overviewRepository.save(overview);

		UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();

		components.getQueryParams()
				.entrySet()
				.forEach(pair -> redirectAttributes.addAttribute(pair.getKey(), pair.getValue()));
		return components.getPath();
	}
}
