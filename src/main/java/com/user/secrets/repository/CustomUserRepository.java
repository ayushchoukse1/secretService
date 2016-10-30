package com.user.secrets.repository;

import java.util.List;

import com.user.secrets.dao.Secret;
import com.user.secrets.dao.User;

public interface CustomUserRepository {
	List<Secret> findAllSecrets(User user);
}
