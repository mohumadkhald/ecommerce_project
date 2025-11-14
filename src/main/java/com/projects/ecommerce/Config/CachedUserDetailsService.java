package com.projects.ecommerce.Config;

import com.projects.ecommerce.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;


@Service
public class CachedUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CachedUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
//    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByEmail(username);
    }
}
