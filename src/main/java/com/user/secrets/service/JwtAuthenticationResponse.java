package com.user.secrets.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JwtAuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 1250166508152483573L;

	private final String token;
	private final String userName;
	private final String validity;

	public JwtAuthenticationResponse(String token,String userName,Date validity) {
		this.token = token;
		this.userName=userName;
		this.validity=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(validity);
	}

	public String getToken() {
		return this.token;
	}

	public String getUserName() {
		return userName;
	}

	public String getValidity() {
		return validity;
	}

	@Override
	public String toString() {
		return "JwtAuthenticationResponse [token=" + token + ", userName=" + userName + ", validity=" + validity + "]";
	}
	
	
}
