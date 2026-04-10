package com.talentmatch.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return switch (username) {
            case "admin" -> User.withUsername("admin")
                    .password("{noop}admin123")
                    .roles("ADMIN")
                    .build();
            case "recruiter" -> User.withUsername("recruiter")
                    .password("{noop}recruiter123")
                    .roles("RECRUITER")
                    .build();
            default -> throw new UsernameNotFoundException("User not found: " + username);
        };
    }
}
