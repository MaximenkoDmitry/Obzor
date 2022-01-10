package com.itransition.obzor.entity;

import com.itransition.obzor.entity.util.OverviewHelper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "overview")
public class Overview {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String title;
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User author;

	private Double rating;
	private String html;

	private Date dateCreated;

	@ManyToMany
	@JoinTable(
			name = "overview_likes",
			joinColumns = { @JoinColumn(name = "overview_id") },
			inverseJoinColumns = { @JoinColumn(name = "user_id")}
	)
	private Set<User> likes = new HashSet<>();

	@ManyToMany
	@JoinTable(
			name = "overview_ratings",
			joinColumns = { @JoinColumn(name = "overview_id") },
			inverseJoinColumns = { @JoinColumn(name = "user_id")}
	)
	private Set<User> ratings = new HashSet<>();

	@CollectionTable(name = "type_overview",joinColumns = @JoinColumn(name = "overview_id"))
	@Enumerated(EnumType.STRING)
	private TypeOfOverview type;

	public String getAuthorName(){
		return OverviewHelper.getCreatorName(author);
	}

}
