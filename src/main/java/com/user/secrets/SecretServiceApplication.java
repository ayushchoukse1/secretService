package com.user.secrets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.user.secrets.domain.Authority;
import com.user.secrets.domain.Secret;
import com.user.secrets.domain.User;
import com.user.secrets.repository.AuthorityRepository;
import com.user.secrets.repository.UserRepository;

@SpringBootApplication
public class SecretServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecretServiceApplication.class, args);
	}

	@Component
	public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
		AuthorityRepository authorityRepository;
		UserRepository userRepository;

		@Autowired
		public StartupListener(AuthorityRepository repo, UserRepository userRepository) {
			this.authorityRepository = repo;
			this.userRepository = userRepository;
		}

		@Override
		public void onApplicationEvent(final ContextRefreshedEvent event) {
			authorityRepository.save(new Authority(1));
			authorityRepository.save(new Authority(2));
			List<Secret> list = new ArrayList<Secret>();
			List<Authority> auth = new ArrayList<Authority>();
			auth.add(authorityRepository.findById((long) 1));
			User admin = new User("admin", "admin", "admin", "admin", "admin@admin.com", true, new Date(), auth, list);
			userRepository.save(admin);
		}
	}
}
