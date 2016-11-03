package com.user.secrets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.user.secrets.domain.Authority;
import com.user.secrets.repository.AuthorityRepository;

@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
	AuthorityRepository authorityRepository;

	@Autowired
	public StartupListener(AuthorityRepository repo) {
		this.authorityRepository = repo;
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		authorityRepository.save(new Authority(1));
		authorityRepository.save(new Authority(2));
	}
}