package com.user.secrets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.user.secrets.domain.Authority;
import com.user.secrets.repository.AuthorityRepository;

@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
	AuthorityRepository authorityRepository;

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	public StartupListener(AuthorityRepository repo) {
		this.authorityRepository = repo;
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		logger.info("Persisting ROLE_ADMIN, ROLE_USER authorities.");
		authorityRepository.save(new Authority(1));
		authorityRepository.save(new Authority(2));
		StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
		strongEncryptor.setPassword("samplePassword");
		HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
		registry.registerPBEStringEncryptor("STRING_ENCRYPTOR", strongEncryptor);
	}
}