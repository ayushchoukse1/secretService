package com.user.secrets.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user.secrets.domain.User;
import com.user.secrets.repository.UserRepository;
import com.user.secrets.security.JwtUserFactory;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
	private final Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
    	System.out.println("findByUserName: "+username);
        
    	User user = userRepository.findByUsername(username);
        
    	System.out.println("loadUserByUsername user is: "+user.toString());
        
    	if (user == null) {
    		logger.error("user with username "+ username+ " doesn't exist.");
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JwtUserFactory.create(user);
        }
    }
}
