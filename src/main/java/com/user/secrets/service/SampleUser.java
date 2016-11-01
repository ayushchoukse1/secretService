/*package com.user.secrets.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SampleUser implements UserDetails{
	
	
	private String password;
	private String username;
	
	
	public SampleUser(String password, String username) {
		super();
		this.password = password;
		this.username = username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return null;
	}

	@Override
	public String getPassword() {
		
		return this.password;
	}

	@Override
	public String getUsername() {
		
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return false;
	}

	@Override
	public boolean isEnabled() {
		
		return false;
	}

}
*/