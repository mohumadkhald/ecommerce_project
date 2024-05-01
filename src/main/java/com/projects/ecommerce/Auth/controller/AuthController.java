package com.projects.ecommerce.Auth.controller;


import com.projects.ecommerce.Auth.dto.AuthResponse;
import com.projects.ecommerce.Auth.dto.LoginRequestDto;
import com.projects.ecommerce.Auth.dto.RegisterRequestDto;
import com.projects.ecommerce.Auth.service.AuthService;
import com.projects.ecommerce.token.TokenRepo;
import com.projects.ecommerce.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /*
    |--------------------------------------------------------------------------
    | Inject The Service Auth
    |--------------------------------------------------------------------------
    |
    */
    private final AuthService authService;
    private final UserService userService;
    private final TokenRepo tokenRepo;
    private final UserRepo userRepo;
    private final EmailVerificationRepo emailVerificationRepo;


    /*
    |--------------------------------------------------------------------------
    | API Routes Register
    |--------------------------------------------------------------------------
    |
    | Here is where you can register API routes for your application. These
    | routes are loaded by the RouteServiceProvider and all of them will
    | be assigned to the "api" middleware group. Make something great!
    | after register you will receive token
    |
    */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequestDto request)
    {
        return ResponseEntity.ok((authService.register(request)));
    }

     /*
    |--------------------------------------------------------------------------
    | API Routes Login
    |--------------------------------------------------------------------------
    |
    | Here is where you can Log in API routes for your application.
    | After login take token to browse in website as you need
    |
    */

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequestDto request) throws Exception {
        return authService.login(request);
    }



    @PostMapping("/logout/all")
    public void logoutAll(@RequestHeader("Authorization") String jwtToken) {
        String token = jwtToken.substring(7); // Extract token from "Bearer TOKEN"
        Integer userId = userService.findUserIdByJwt(jwtToken);
            // Retrieve the tokens from the repository
        var validTokensForUser = tokenRepo.findAllValidTokenByUser(userId);
        if (validTokensForUser.isEmpty())
            return;
        validTokensForUser.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepo.saveAll(validTokensForUser);
    }





    @GetMapping("/verify-email")
    public ModelAndView verifyEmail(@RequestParam("token") String token) {
        // Find user by verification token
        EmailVerification emailVerification = emailVerificationRepo.findByVerificationToken(token);
        if (emailVerification != null && emailVerification.getVerificationTokenExpiry().isAfter(LocalDateTime.now())) {
            User user = emailVerification.getUser();
            // Mark user's email as verified
            emailVerification.setEmailVerified(true);
            // Clear verification token and expiry
            emailVerification.setVerificationToken(null);
            emailVerification.setVerificationTokenExpiry(null);
            // Save changes
            userRepo.save(user);
            emailVerificationRepo.save(emailVerification);
            // Redirect to http://localhost:3000/
            return new ModelAndView("redirect:http://localhost:3000/");
        } else {
            // Redirect to an error page
            return new ModelAndView("redirect:/invalid-token");
        }
    }


    @RequestMapping("/invalid-token")
    public String invalidToken() {
        return "Invalid or expired token.";
    }

    @PostMapping("resend-verify")
    public ResponseEntity<?> sendEmailVerify(@RequestHeader ("Authorization") String token) throws Exception {
        int userID = userService.findUserIdByJwt(token);
        User user = userService.findById(userID);
        return authService.resendVerificationEmail(user.getEmail());
    }

}
