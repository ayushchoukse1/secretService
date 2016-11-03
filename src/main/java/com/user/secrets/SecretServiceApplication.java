package com.user.secrets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.user.secrets.domain.Authority;
import com.user.secrets.repository.AuthorityRepository;

@SpringBootApplication
public class SecretServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecretServiceApplication.class, args);
	}

	@Component
	public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
		AuthorityRepository repo;

		@Autowired
		public StartupListener(AuthorityRepository repo) {
			this.repo = repo;
		}

		@Override
		public void onApplicationEvent(final ContextRefreshedEvent event) {
			repo.save(new Authority(1));
			repo.save(new Authority(2));
		}
	}
}
