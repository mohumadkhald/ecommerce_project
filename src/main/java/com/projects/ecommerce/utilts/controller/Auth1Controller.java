package com.projects.ecommerce.utilts.controller;

import com.projects.ecommerce.Auth.dto.AuthResponse;
import com.projects.ecommerce.Auth.dto.LoginRequestDto;
import com.projects.ecommerce.Auth.dto.RegisterRequestDto;
import com.projects.ecommerce.Auth.service.AuthService;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
public class Auth1Controller {
    private final UserService userService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public RedirectView login() {
        return new RedirectView("/oauth2/authorization/google");
    }

    @GetMapping("/loginSuccess")
    public ModelAndView loginSuccess(@AuthenticationPrincipal OidcUser oidcUser, @AuthenticationPrincipal OAuth2User oauth2User) throws Exception {
        String email;
        String familyName;
        String givenName;
        String pictureUrl;
        String name;
        String gender;

        if (oidcUser != null) {
            email = oidcUser.getEmail();
            familyName = oidcUser.getFamilyName();
            givenName = oidcUser.getGivenName();
            pictureUrl = oidcUser.getPicture();
            name = oidcUser.getName();
            gender = oidcUser.getGender();
        } else if (oauth2User != null) {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            givenName = null;
            familyName = null;
            if (name != null) {
                String[] nameParts = name.split(" ");
                if (nameParts.length > 0) {
                    givenName = nameParts[0]; // First part is given name
                }
                if (nameParts.length > 1) {
                    familyName = nameParts[nameParts.length - 1]; // Last part is family name
                }
            }
            pictureUrl = null;
            Map<String, Object> pictureObj = oauth2User.getAttribute("picture");
            if (pictureObj != null) {
                Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
                if (dataObj != null) {
                    pictureUrl = (String) dataObj.get("url");
                }
            }
            name = oauth2User.getAttribute("name");
            gender = oauth2User.getAttribute("gender");
        } else {
            throw new Exception("Authentication principal is missing");
        }

        ModelAndView modelAndView = new ModelAndView("redirect:http://localhost:4200/login");

        // Check if user already exists in your system
        User existingUser = userService.findByEmail(email);

        // Default password indicating the user needs to set their own password
        String defaultPassword = "defaultPassword";

        if (existingUser == null) {
            log.info("creating new user");
            RegisterRequestDto registerRequestDto = new RegisterRequestDto();
            registerRequestDto.setEmail(email);
            registerRequestDto.setPasswordOauth2("b~>&^L^G^8GZXXd");
            registerRequestDto.setPassword("password");
            registerRequestDto.setO2Auth(true);
            registerRequestDto.setFirstname(givenName);
            registerRequestDto.setLastname(familyName);
            registerRequestDto.setGender(gender);
            registerRequestDto.setImg(pictureUrl);
            AuthResponse authResponse = authService.register(registerRequestDto);

            User newUser = userService.findByEmail(email);
            newUser.setNeedsToSetPassword(true);
            userService.save(newUser);

            modelAndView.addObject("token", authResponse.getToken());
            modelAndView.addObject("message", "Login Success");
            modelAndView.addObject("role", authResponse.getRole());
        } else {
            log.info("user already exists");
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setEmail(email);
            loginRequestDto.setPasswordOauth2("b~>&^L^G^8GZXXd");
            loginRequestDto.setRemember(true);
            loginRequestDto.setO2Auth(true);
            AuthResponse authResponse = authService.login(loginRequestDto);

            modelAndView.addObject("token", authResponse.getToken());
            modelAndView.addObject("message", "Login Success");
            modelAndView.addObject("role", authResponse.getRole());
        }


        boolean isNewUser = existingUser == null;
        if (isNewUser) {
            modelAndView.addObject("newUser", "true");
        } else {
            modelAndView.addObject("newUser", existingUser.isNeedsToSetPassword() ? "true" : "false");
        }


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