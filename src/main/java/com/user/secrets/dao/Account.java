package com.user.secrets.dao;

import java.util.List;

public class Account {

	private User user;
	private List<Secret> secrets;

	/**
	 * 
	 */
	public Account() {
	}

	/**
	 * @param user
	 * @param secrets
	 */
	public Account(User user, List<Secret> secrets) {
		super();
		this.user = user;
		this.secrets = secrets;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the secrets
	 */
	public List<Secret> getSecrets() {
		return secrets;
	}

	/**
	 * @param secrets
	 *            the secrets to set
	 */
	public void setSecrets(List<Secret> secrets) {
		this.secrets = secrets;
	}

}
