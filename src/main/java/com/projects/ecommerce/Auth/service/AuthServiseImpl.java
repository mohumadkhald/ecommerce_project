package com.projects.ecommerce.Auth.service;

import com.projects.ecommerce.Auth.dto.AuthResponse;
import com.projects.ecommerce.Auth.dto.LoginRequestDto;
import com.projects.ecommerce.Auth.dto.RegisterRequestDto;

import com.projects.ecommerce.Auth.expetion.AuthenticationnException;
import com.projects.ecommerce.Config.JwtService;
import com.projects.ecommerce.Auth.token.Token;
import com.projects.ecommerce.Auth.token.TokenRepo;
import com.projects.ecommerce.Auth.token.TokenType;
import com.projects.ecommerce.mail.EmailService;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
import com.projects.ecommerce.user.model.AccountStatus;
import com.projects.ecommerce.user.model.EmailVerification;
import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthServiseImpl implements AuthService {

    /*
    |--------------------------------------------------------------------------
    | Inject Classes
    |--------------------------------------------------------------------------
    |
    | Get Number of Followers And Following and Get Friends
    |
    */
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenRepo tokenRepo;
    private EmailService emailService;
    private final ApiTrait apiTrait;
    /*
    |--------------------------------------------------------------------------
    | Implement Register
    |--------------------------------------------------------------------------
    |
    | In her you can Controller the data you want to enter to db
    |
    */
    @Override
    public AuthResponse register(RegisterRequestDto request) {

        if (request.isO2Auth()){
            request.setRole(Role.ADMIN);
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .passwordOauth2(passwordEncoder.encode(request.getPasswordOauth2()))
//                .role(Role.USER)
                .role(request.getRole())
                .gender(request.getGender())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(request.getEmail())
                .updatedBy(request.getEmail())
                .needsToSetPassword(request.isO2Auth())
//                .phone(request.getPhone())
                .isO2Auth(request.isO2Auth())
                .imgUrl(request.getImg())
                .build();

        // Create EmailVerification entity
        EmailVerification emailVerification = new EmailVerification();
        if (request.isO2Auth()){
            emailVerification.setEmailVerified(true);
        } else {
            emailVerification.setEmailVerified(false);
        }
        emailVerification.setUser(user);
//        emailVerification.setVerificationToken(UUID.randomUUID().toString());
//        emailVerification.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24)); // Set expiry time (1 day)
        user.setEmailVerification(emailVerification);

        // account status for user
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.setUser(user);
        accountStatus.setAccountNonExpired(true);
        accountStatus.setAccountNonLocked(true);
        accountStatus.setCredentialsNonExpired(true);
        user.setAccountStatus(accountStatus);

        if (userRepo.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email", "already exists");
        }

        var savedUser = userRepo.save(user);
        int expirationDay = getExpirationDay(false);
        var jwtToken = jwtService.generateToken(savedUser, expirationDay); // Generate token for savedUser
        savedUserToken(savedUser, jwtToken, false);
//        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getEmailVerification().getVerificationToken()); // Send verification email using EmailVerification entity
        return AuthResponse.builder().role(String.valueOf(user.getRole())).token(jwtToken).message("Register Success Have A Nice Time").build();
    }

    @Override
    public ResponseEntity<?> resendVerificationEmail(String email) throws Exception {
        User user = userRepo.findByEmail(email);

        if (user.getEmailVerification().isEmailVerified()) {
            return  ApiTrait.successMessage("Email Already Verify", HttpStatus.FOUND);
        }

        // Update verification token and expiry time using EmailVerification entity
        EmailVerification emailVerification = user.getEmailVerification();
        emailVerification.setVerificationToken(UUID.randomUUID().toString());
        emailVerification.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24)); // Set expiry time (1 day)
        user.setEmailVerification(emailVerification);

        userRepo.save(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerification().getVerificationToken()); // Send verification email using EmailVerification entity
        return  ApiTrait.successMessage("Email Send Success", HttpStatus.ACCEPTED);

    }




    /*
    |--------------------------------------------------------------------------
    | Implement Log in
    |--------------------------------------------------------------------------
    |
    | Take Data you need from auth and have all information to check any think you need
    |
    */
    public AuthResponse login(LoginRequestDto request) throws Exception {
        try {

            // Retrieve user details
            var user = userRepo.findByEmail(request.getEmail());
            if (request.isO2Auth())
            {
                user.setO2Auth(true);
                user.setUpdatedBy(request.getEmail());
                EmailVerification emailVerification =  user.getEmailVerification();
                emailVerification.setEmailVerified(true);
                userRepo.save(user);
                request.setPassword(request.getPasswordOauth2());
            }

            // Authenticate user
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));


            int expirationDay = getExpirationDay(request.isRemember());

            // Generate JWT token
            var jwtToken = jwtService.generateToken(user, expirationDay);
            savedUserToken(user, jwtToken, request.isRemember());

            if (!user.getEmailVerification().isEmailVerified())
            {
                return AuthResponse.builder().token(jwtToken).role(user.getRole().toString()).message("Login Success Email " + user.getEmail() + " Not Verify").build();
            }
            // Return the token along with the user details
            return AuthResponse.builder().token(jwtToken).role(user.getRole().toString()).message("Login Success").build();
        } catch (AuthenticationException e) {

            String errorMessage = getString(e);
            throw new AuthenticationnException(errorMessage) {

            };
        }
    }

    // error message when error in login
    private static String getString(AuthenticationException e) {
        String errorMessage = e.getMessage();
        if (Objects.equals(errorMessage, "UserDetailsService returned null, which is an interface contract violation")) {
            errorMessage = "please register to continue";
        } else if (Objects.equals(errorMessage, "User credentials have expired")) {
            errorMessage = "User credentials have expired Please Reset Your Password";
        } else if (Objects.equals(errorMessage, "User account has expired")) {
            errorMessage = "User account has expired Please Contact Support Service";
        } else if (Objects.equals(errorMessage, "User account is locked")) {
            errorMessage = "User account is locked Please Contact Support Service";
        } else if (Objects.equals(errorMessage, "Bad credentials")) {
            errorMessage = "Invalid Credentials";
        }
        return errorMessage;
    }

    // set expiry date for token
    private static int getExpirationDay(boolean request) {
        int expirationDay;
        if (request)
        {
            expirationDay = 1000 * 60 * 60 * 24 * 7; // check Remember Me token Valid 7 Days or when logout
        } else {
            //expirationDay = 1000 * 60 * 60 * 24; // If Not check RememberMe token valid 24 Hour or when logout
            expirationDay = 1000 * 60 * 60 * 24;
        }
        return expirationDay;
    }

    // Saved any token when user register or login
    private void savedUserToken(User user, String jwtToken, boolean status) {
        int expirationDay = getExpirationDay(status); // Get expiration day based on status
        LocalDateTime expirationDateTime = LocalDateTime.now().plusDays(expirationDay / (24 * 60 * 60 * 1000));
        List<Token> notValidTokensByUser = tokenRepo.findAllNotValidTokensByUser(user.getId());
        tokenRepo.deleteAll(notValidTokensByUser);
        var myToken = Token.builder()
                .user(user)
                .token(jwtToken)
                .rememberMe(status)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .expirationDate(expirationDateTime)
                .build();
        tokenRepo.save(myToken);
    }





    // if you need make only for user one token call this method on login
    private void revokeAllUserToken(User user)
    {
        var validTokensForUser = tokenRepo.findAllValidTokenByUser(user.getId());
        if (validTokensForUser.isEmpty())
            return;
        validTokensForUser.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepo.saveAll(validTokensForUser);
    }



}
