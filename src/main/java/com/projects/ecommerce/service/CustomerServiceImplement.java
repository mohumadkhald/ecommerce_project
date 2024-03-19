package com.projects.ecommerce.service;

import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.UserRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Service
@Setter
public class CustomerServiceImplement implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomerServiceImplement(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username); // Corrected method call
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with email: " + username);
        }

        // Populate authorities based on user roles/permissions
        List<GrantedAuthority> authorityList = getUserAuthorities(user);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorityList
        );
    }

    private List<GrantedAuthority> getUserAuthorities(User user) {
        // Implement logic to fetch user authorities (roles/permissions) from the database
        // and convert them into a list of GrantedAuthority objects
        // For example:
        // List<GrantedAuthority> authorities = user.getRoles()
        //                                         .stream()
        //                                         .map(role -> new SimpleGrantedAuthority(role.getName()))
        //                                         .collect(Collectors.toList());
        // Replace this with your actual logic
        return Collections.emptyList(); // Return an empty list for now
    }

}
