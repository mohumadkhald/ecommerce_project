package com.projects.ecommerce.utilts.controller;

import com.projects.ecommerce.Auth.dto.AuthResponse;
import com.projects.ecommerce.Auth.dto.LoginRequestDto;
import com.projects.ecommerce.Auth.service.AuthService;
import com.projects.ecommerce.Config.JwtService;
import com.projects.ecommerce.user.model.AccountStatus;
import com.projects.ecommerce.user.model.EmailVerification;
import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class Auth1Controller {
    private final UserService userService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public RedirectView login() {
        return new RedirectView("/oauth2/authorization/google");
    }

    @GetMapping("/loginSuccess")
    public ModelAndView loginSuccess(@AuthenticationPrincipal OidcUser oidcUser) throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(oidcUser.getEmail());
        loginRequestDto.setPassword("123456Ax#");
        loginRequestDto.setRemember(true);

        // Check if user already exists in your system
        User existingUser = userService.findByEmail(oidcUser.getEmail());
        if (existingUser == null) {
            // Create new user if not exists
            User newUser = new User();
            newUser.setEmail(oidcUser.getEmail());
            newUser.setPassword(passwordEncoder.encode("123456Ax#"));
            newUser.setLastname(oidcUser.getFamilyName());
            newUser.setFirstname(oidcUser.getGivenName());
            newUser.setGender(oidcUser.getGender());
            newUser.setImgUrl(oidcUser.getPicture());
            newUser.setCreatedBy(oidcUser.getName());

            // Set account status and email verification
            AccountStatus accountStatus = new AccountStatus();
            accountStatus.setUser(newUser);
            accountStatus.setAccountNonExpired(true);
            accountStatus.setAccountNonLocked(true);
            accountStatus.setCredentialsNonExpired(true);
            newUser.setAccountStatus(accountStatus);

            EmailVerification emailVerification = new EmailVerification();
            emailVerification.setEmailVerified(true);
            emailVerification.setUser(newUser);
            newUser.setEmailVerification(emailVerification);

            // Set role and save user
            newUser.setRole(Role.USER);
            userService.save(newUser);
        }

        // Perform login and retrieve authentication response
        AuthResponse authResponse = authService.login(loginRequestDto);

        // Create ModelAndView and add attributes
        ModelAndView modelAndView = new ModelAndView("redirect:http://localhost:4200/login");
        modelAndView.addObject("token", authResponse.getToken());
        modelAndView.addObject("message", "Login Success");
        modelAndView.addObject("role", authResponse.getRole());

        return modelAndView;
    }

    @GetMapping("/api/userinfo")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", oidcUser.getFullName());
        userInfo.put("email", oidcUser.getEmail());
        userInfo.put("picture", oidcUser.getPicture());

        return userInfo;
    }

}