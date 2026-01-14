package edu.miage.springboot.services.impl;

import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.web.dtos.auth.AuthUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return new AuthUserDetails(user);
    }
}
