package com.user.secrets.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.user.secrets.domain.Authority;
import com.user.secrets.domain.User;

public final class JwtUserFactory {

	private JwtUserFactory() {
	}

	public static JwtUser create(User user) {
		System.out.println("create JWT USER: "+user.toString());
		return new JwtUser(user.getId(), 
				user.getUsername(), 
				user.getFirstname(), 
				user.getLastname(), 
				user.getEmail(),
				user.getPassword(), 
				mapToGrantedAuthorities(user.getAuthorities()), 
				user.getEnabled(),
				user.getLastPasswordResetDate());
	}

	private static List<GrantedAuthority> mapToGrantedAuthorities(List<Authority> authorities) {
		return authorities.stream().map(authority -> new SimpleGrantedAuthority("ROLE_USER"))
				.collect(Collectors.toList());
	}
}
