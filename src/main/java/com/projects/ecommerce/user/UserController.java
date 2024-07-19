package com.projects.ecommerce.user;

import com.projects.ecommerce.Auth.dto.RegisterRequestDto;
import com.projects.ecommerce.Auth.dto.UpdateUserRequestDto;
import com.projects.ecommerce.Auth.token.TokenRepo;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.dto.UserResponseDto;
import com.projects.ecommerce.user.model.AccountStatus;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.user.service.UserService;
import com.projects.ecommerce.utilts.FileStorageService;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200/")
public class UserController {

    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final UserService userService;




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
    @PostMapping
    public UserResponseDto register(@Valid @RequestBody RegisterRequestDto dto)
    {
        return  this.userService.registerUser(dto);
    }






    /*
    |--------------------------------------------------------------------------
    | API Routes Search about Email
    |--------------------------------------------------------------------------
    */
    @GetMapping("/search/{email}")
    public ResponseEntity<?> findUserByEmail(@PathVariable String email)
    {
        return userService.getUserByEmailResponse(email);
    }


    /*
    |--------------------------------------------------------------------------
    | API Routes Edit Data of User
    |--------------------------------------------------------------------------
    */




    /*
    |--------------------------------------------------------------------------
    | API Routes Edit Data of User
    |--------------------------------------------------------------------------
    */
    @PutMapping("/{id}")
    public ResponseEntity<?> editUser(@PathVariable Integer id, @RequestBody UpdateUserRequestDto newData)
    {

        return userService.updateUser(id, newData);

    }



    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(
            @PathVariable("userId")
            @Valid final String userId) {
        return ResponseEntity.ok(this.userService.findById(Integer.parseInt(userId.strip())));
    }



    /*
    |--------------------------------------------------------------------------
    | API Routes Delete User Not implement and need to remove any following first
    |--------------------------------------------------------------------------
    */
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            // Delete associated tokens (assuming 'tokens' is another entity with a foreign key to 'users')
            tokenRepo.deleteByUserId(id);

            // Now delete the user
            userRepo.deleteById(id);
            return ApiTrait.successMessage("User with " + id + " Deleted", HttpStatus.OK);
        }
        return ResponseEntity.ok("User not found");
    }




    /*
    |--------------------------------------------------------------------------
    | API Routes Get AllUser
    |--------------------------------------------------------------------------
    */
    @GetMapping
    public ResponseEntity<?> getUsers()
    {
        return userService.getAllUsers();
    }






    /*
    |--------------------------------------------------------------------------
    | API Routes Search about User by firstname or email or phone
    |--------------------------------------------------------------------------
    */
    @GetMapping("search")
    public ResponseEntity<?> search(@RequestParam("query") String query)
    {
        return userService.searchUser(query);
    }





    @PatchMapping("/status/{id}")
    public ResponseEntity<?> changeStatus(
            @PathVariable Integer id,
            @RequestParam(required = false) Boolean accNonLocked,
            @RequestParam(required = false) Boolean accNonExpire,
            @RequestParam(required = false) Boolean credentialNonExpire) {

        log.info("Received request to change status for user with id: {}", id);

        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            AccountStatus accountStatus = user.getAccountStatus();

            if (accNonLocked != null) {
                accountStatus.setAccountNonLocked(accNonLocked);
            }
            if (accNonExpire != null) {
                accountStatus.setAccountNonExpired(accNonExpire);
            }
            if (credentialNonExpire != null) {
                accountStatus.setCredentialsNonExpired(credentialNonExpire);
            }

            accountStatus.setUser(user);
            user.setAccountStatus(accountStatus);
            userRepo.save(user);

            log.info("User with id: {} updated successfully", id);
            return ResponseEntity.ok(ApiTrait.successMessage("User with id " + id + " updated", HttpStatus.OK));
        } else {
            log.warn("User with id: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}


