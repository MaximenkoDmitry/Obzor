package com.itransition.obzor.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String surname;
	private String username;
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	private Long numberOfLikes;
}
