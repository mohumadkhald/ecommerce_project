package com.projects.ecommerce.user;

import com.projects.ecommerce.Auth.dto.RegisterRequestDto;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.dto.UserResponseDto;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000/")
public class UserController {

    private final UserRepo userRepo;
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
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String jwtToken,@Valid @RequestBody RegisterRequestDto newData)
    {

        Integer id = userService.findUserIdByJwt(jwtToken);
        return userService.updateUser(id, newData);

    }



    /*
    |--------------------------------------------------------------------------
    | API Routes Edit Data of User
    |--------------------------------------------------------------------------
    */
    @PutMapping("/{id}")
    public ResponseEntity<?> editUser(@PathVariable Integer id,@Valid @RequestBody RegisterRequestDto newData)
    {

        return userService.updateUser(id, newData);

    }



//    @GetMapping("/{userId}")
//    public ResponseEntity<UserDto> findById(
//            @PathVariable("userId")
//            @NotBlank(message = "Input must not blank")
//            @Valid final String userId) {
//        return ResponseEntity.ok(this.userService.findById(Integer.parseInt(userId.strip())));
//    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> findById(@RequestHeader ("Authorization") String token){
        int userID = userService.findUserIdByJwt(token);
        return ResponseEntity.ok(this.userService.findById(userID));
    }

    /*
    |--------------------------------------------------------------------------
    | API Routes Delete User Not implement and need to remove any following first
    |--------------------------------------------------------------------------
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id)
    {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent())
        {
            userRepo.deleteById(id);
            return ResponseEntity.ok("user deleted");
        }
        return ResponseEntity.ok("user not found");
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







}
