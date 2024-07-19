package com.projects.ecommerce.Auth.controller;


import com.projects.ecommerce.Auth.dto.*;
import com.projects.ecommerce.Auth.service.AuthService;
import com.projects.ecommerce.Auth.token.TokenRepo;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.model.EmailVerification;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.repository.EmailVerificationRepo;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.user.service.UserService;
import com.projects.ecommerce.utilts.FileStorageService;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
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
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;



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
        request.setO2Auth(false);
        request.setImg(null);
        request.setPasswordOauth2("b~>&^L^G^8GZXXd");
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
        request.setO2Auth(false);
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
            t.setExpirationDate(LocalDateTime.now());
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


    @GetMapping("/invalid-token")
    public String invalidToken() {
        return "Invalid or expired token.";
    }

    @PostMapping("resend-verify")
    public ResponseEntity<?> sendEmailVerify(@RequestHeader ("Authorization") String token) throws Exception {
        int userID = userService.findUserIdByJwt(token);
        UserDto user = userService.findById(userID);
        return authService.resendVerificationEmail(user.getEmail());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> findById(@RequestHeader ("Authorization") String token){
        int userID = userService.findUserIdByJwt(token);
        return ResponseEntity.ok(this.userService.findById(userID));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String jwtToken,@Valid @RequestBody UpdateUserRequestDto newData)
    {

        Integer id = userService.findUserIdByJwt(jwtToken);
        return userService.updateUser(id, newData);

    }


    @PatchMapping("setFirstPwd")
    public ResponseEntity<?> setFirstPwd(@RequestHeader("Authorization") String jwtToken,@Valid @RequestBody FirstPasswordDto firstPasswordDto)
    {

        Integer id = userService.findUserIdByJwt(jwtToken);
        User user = userService.findByUserId(id);
        user.setPassword(passwordEncoder.encode(firstPasswordDto.getPassword()));
        userRepo.save(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "The password has been set");
        return ResponseEntity.ok(response);
    }


    @PatchMapping("photo")
    public ResponseEntity<?> changePhoto(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader("Authorization") String jwtToken)
            throws IOException {

        Integer userId = userService.findUserIdByJwt(jwtToken);
        log.info("*** ProductDto, resource; save product ***");

        // Check if the image is null or empty and add a global error
        if (image == null || image.isEmpty()) {
            throw new IllegalStateException("Image file is required");
        }

        String imageUrl = fileStorageService.storeFile(image, "users/" + userId);
        userService.updateUserPhoto(userId, imageUrl);

        return ApiTrait.successMessage(imageUrl, HttpStatus.OK);
    }

}
