package com.projects.ecommerce.user.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projects.ecommerce.Auth.token.Token;
import com.projects.ecommerce.utilts.Base;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "users")
public class User extends Base implements UserDetails {

    private String firstname;
    private String lastname;
    private String gender;
    private String phone;
    private String email;
    private String password;
    private String passwordOauth2;
    private String imgUrl;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Address> addresses;

    // Relationship with PasswordReset entity
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PasswordReset passwordReset;

    // Relationship with EmailVerification entity
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmailVerification emailVerification;

    // Relationship with AccountStatus entity
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    private boolean isO2Auth;

    // very important
    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, email); // Include relevant fields for uniqueness
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public String getPassword() {
        if (isO2Auth)
        {
            return passwordOauth2;
        }
        return password;
    }
    @Override
    public boolean isAccountNonExpired() {
        return accountStatus != null && accountStatus.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != null && accountStatus.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accountStatus != null && accountStatus.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
