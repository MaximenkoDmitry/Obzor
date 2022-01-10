package com.itransition.obzor;

import com.itransition.obzor.entity.Role;
import com.itransition.obzor.entity.User;
import com.itransition.obzor.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class test {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository repo;
	@Test
	public void testCreateUser() {
		User user = new User();
		user.setUsername("username");
		user.setPassword("password");
		user.setRole(Role.USER);

		User savedUser = repo.save(user);

		User existUser = entityManager.find(User.class, savedUser.getId());

		assertThat(user.getUsername()).isEqualTo(existUser.getUsername());

	}
}
