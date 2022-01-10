package com.itransition.obzor.service;
import com.itransition.obzor.entity.Overview;
import com.itransition.obzor.entity.Role;
import com.itransition.obzor.entity.User;
import com.itransition.obzor.repo.OverviewRepository;
import com.itransition.obzor.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private OverviewRepository overviewRepository;
	@Autowired
	private UserRepository userRepo;

	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	public CustomUserDetailsService() {

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new CustomUserDetails(user);
	}


	public List<User> allUsers() {
		return userRepo.findAll();
	}

	public boolean saveUser(User user) {
		User userFromDB = userRepo.findByUsername(user.getUsername());

		if (userFromDB != null) {
			return false;
		}

		user.setNumberOfLikes(new Long(0));
		user.setRole(Role.USER);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepo.save(user);
		return true;
	}

	public boolean deleteUser(Long userId) {
		if (userRepo.findById(userId).isPresent()) {
			userRepo.deleteById(userId);
			return true;
		}
		return false;
	}

	public User getAuthUser(){
		return userRepo.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	public Long getLikesOfUser(User user){
		Long result = new Long(0);
		List <Overview> overviews = overviewRepository.findAll();
		for (Overview overview: overviews
			 ) {
			if(overview.getLikes().contains(user))
				result++;
		}
		return result;

	}
}