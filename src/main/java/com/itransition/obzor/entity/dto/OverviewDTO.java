package com.itransition.obzor.entity.dto;

import com.itransition.obzor.entity.Overview;
import com.itransition.obzor.entity.TypeOfOverview;
import com.itransition.obzor.entity.User;
import com.itransition.obzor.entity.util.OverviewHelper;
import lombok.Getter;

import java.util.Comparator;
import java.util.Date;

@Getter
public class OverviewDTO {
	private Long id;
	private String title;
	private TypeOfOverview type;
	private String description;
	private Date dateCreated;
	private String html;
	private User author;
	private Double rating;
	private Long likes;
	private Boolean meLiked;

	public OverviewDTO(Overview overview, Long likes, Boolean meLiked) {
		this.id = overview.getId();
		this.title = overview.getTitle();
		this.description = overview.getDescription();
		this.author = overview.getAuthor();
		this.rating = overview.getRating();
		this.type = overview.getType();
		this.dateCreated = overview.getDateCreated();
		this.html = overview.getHtml();
		this.likes = likes;
		this.meLiked = meLiked;
	}

	public String getAuthorName(){
		return OverviewHelper.getCreatorName(author);
	}

	@Override
	public String toString() {
		return "OverviewDto{" +
				"id=" + id +
				", author=" + OverviewHelper.getCreatorName(author) +
				", likes=" + likes +
				", title=" + title +
				", likes=" + likes +
				", meLiked=" + meLiked +
				'}';
	}

	public static final Comparator<OverviewDTO> COMPARE_BY_RATING = new Comparator<OverviewDTO>() {
		@Override
		public int compare(OverviewDTO lhs, OverviewDTO rhs) {
			return lhs.getRating().compareTo(rhs.getRating());
		}
	};
}
