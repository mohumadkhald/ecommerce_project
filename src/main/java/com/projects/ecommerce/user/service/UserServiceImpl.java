package com.projects.ecommerce.user.service;

import com.projects.ecommerce.Auth.dto.ChangePasswordDto;
import com.projects.ecommerce.Auth.dto.RegisterRequestDto;
import com.projects.ecommerce.Auth.dto.UpdateUserRequestDto;
import com.projects.ecommerce.Config.JwtService;
import com.projects.ecommerce.user.expetion.UserNotFoundException;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import com.projects.ecommerce.user.UserMapper;

import com.projects.ecommerce.user.UserMappingHelper;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.user.dto.UserResponseDto;
import com.projects.ecommerce.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {


    /*
    |--------------------------------------------------------------------------
    | Inject Of  Class
    |--------------------------------------------------------------------------
    |
    | Here is where you can Inject Class Service and Repository and Another Providers
    |
    */
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final JwtService jwtProvider;
    private final PasswordEncoder passwordEncoder;


    /*|--------------------------------------------------------------------------
            | End of Inject
    |-------------------------------------------------------------------------- */




    /*|--------------------------------------------------------------------------
        | Start Implements All Method
    |-------------------------------------------------------------------------- */

    @Override
    public Integer findUserIdByJwt(String jwt)
    {
        String email = jwtProvider.getEmailFromToken(jwt);
        User user = userRepo.findByEmail(email);

        if (user == null) {
            return null;
        }

        return user.getId();
    }


    @Override
    public UserDto findById(final Integer userId) {
        return this.userRepo.findById(userId)
                .map(UserMappingHelper::map)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %d not found", userId)));
    }

    /*|--------------------------------------------------------------------------
                                    | End Implement
    |-------------------------------------------------------------------------- */




    /*
    |--------------------------------------------------------------------------
    | Create New User
    |--------------------------------------------------------------------------
    |
    | Here is How you can Create New account For User
    |
    */
    @Override
    public UserResponseDto registerUser(RegisterRequestDto dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException("Email", "already exists");
        }
        var user = userMapper.toUser(dto);
        userRepo.save(user);
        return userMapper.toUserResponseDto(user);
    }

    /*|--------------------------------------------------------------------------
                                    | End Implement
    |-------------------------------------------------------------------------- */




    /*
    |--------------------------------------------------------------------------
    | Implement Of Retrieve User
    |--------------------------------------------------------------------------
    |
    | Here is How you can Get user by ID
    |
    */
    @Override
    public ResponseEntity<?> getUserByIdResponse(Integer id) {
        List<UserResponseDto> user = Collections.singletonList(findUserById(id));
        return ApiTrait.data(user,"The Data For User Retrieved Success", HttpStatus.OK);

    }

    private UserResponseDto findUserById(Integer id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return userOptional.map(userMapper::toUserResponseDto).orElse(null);
    }



    /*|--------------------------------------------------------------------------
                                    | End Implement
    |-------------------------------------------------------------------------- */




    /*
    |--------------------------------------------------------------------------
    | Implement How Can Search Email
    |--------------------------------------------------------------------------
    |
    | Here is How you can Search about User using Email Address
    |
    */
    @Override
    public ResponseEntity<?> getUserByEmailResponse(String email) {
        UserResponseDto user = findUserByEmail(email);
        return ApiTrait.data(Collections.singletonList(user), "The Data For User Retrieved Success", HttpStatus.OK);
    }

    private UserResponseDto findUserByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            return userMapper.toUserResponseDto(user);
        }
        throw new UserNotFoundException("The user not found " + email);
    }

    /*|--------------------------------------------------------------------------
                                | End Implement
    |-------------------------------------------------------------------------- */





    /*
    |--------------------------------------------------------------------------
    | Implement How Can Search about User
    |--------------------------------------------------------------------------
    |
    | Here is How you can get all Users Using List And Hash Map
    |
    */

    @Override
    public ResponseEntity<?> searchUser(String query) {
        List<UserResponseDto> users = findUsers(query);
        if (users.isEmpty()) {
            return ApiTrait.errorMessage(new HashMap<>(), "No users found", HttpStatus.NOT_FOUND);
        } else {
            return ApiTrait.data(users, "The Data Retrieved Success", HttpStatus.OK);
        }
    }

    private List<UserResponseDto> findUsers(String query) {
        return userRepo.searchUser(query)
                .stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    /*|--------------------------------------------------------------------------
            | End Implement
    |-------------------------------------------------------------------------- */




    /*
    |--------------------------------------------------------------------------
    | Implement How Can Get All Users
    |--------------------------------------------------------------------------
    |
    | Here is How you can get all Users Using List And Hash Map After Get Collectors
    |
    */
    @Override
    public ResponseEntity<Page<?>> getAllUsers(int page, int pageSize, Sort sort) {
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<UserResponseDto> users = findAllUsers(pageable);

        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    private Page<UserResponseDto> findAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(userMapper::toUserResponseDto);
    }

    /*|--------------------------------------------------------------------------
                                       | End Implement
    |-------------------------------------------------------------------------- */




    /*
    |--------------------------------------------------------------------------
    | Implement How Can Edit User
    |--------------------------------------------------------------------------
    |
    | Here is How you can Edit user and make all checks if email exist
    |
    */
    @Override
    public ResponseEntity<?> updateUser(Integer id, UpdateUserRequestDto dto) {
        try {

            // Retrieve the user by id
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

            // Check if the email is being updated
            if (!user.getEmail().equals(dto.getEmail())) {
                // If the email is being updated, check if the new email already exists
                User existingUserByEmail = userRepo.findByEmail(dto.getEmail());
                if (existingUserByEmail != null && !existingUserByEmail.getId().equals(id)) {
                    // If the new email already exists and belongs to a different user, throw an exception
                    throw new AlreadyExistsException("Email", "Already Exists: " + dto.getEmail());
                }
            }

            // Update user fields
            user.setFirstname(dto.getFirstName());
            user.setLastname(dto.getLastName());
            user.setGender(dto.getGender());
            user.setEmail(dto.getEmail());

            // Save the updated user
            userRepo.save(user);

            // Return success response
            return ApiTrait.successMessage("User updated successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            // Return error response for user not found
            throw new UserNotFoundException("User not found with id: " + id);
        } catch (AlreadyExistsException e) {
            // Return error response for email already exists
            return ApiTrait.errorMessage(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            // Return error response for validation errors
            return ApiTrait.errorMessage(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Return error response for incorrect old password
            return ApiTrait.errorMessage(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Return error response for unexpected errors
            return ApiTrait.errorMessage(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void save(User newUser) {
         userRepo.save(newUser);
    }

    @Override
    public User findByUserId(Integer userID) {
        return userRepo.findUserById(userID);
    }

    @Override
    public void updateUserPhoto(Integer userId, String imageUrl) {
        User user = userRepo.findUserById(userId);
        user.setImgUrl(imageUrl);
        userRepo.save(user);
    }

    @Override
    public ResponseEntity<?> changePassword(Integer id, ChangePasswordDto dto) {
        try {
            // Validate DTO using @Valid annotation and handle validation errors
            if (dto.getOldPassword() == null || dto.getOldPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Old password cannot be empty");
            }

            // Retrieve the user by id
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));



            // Check if the provided old password matches the user's current password
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Wrong password");
            }

            // Check if password is being updated
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                // Encode and update password
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            // Save the updated user
            userRepo.save(user);

            // Return success response
            return ApiTrait.successMessage("User updated successfully", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            // Return error response for user not found
            throw new UserNotFoundException("User not found with id: " + id);
        } catch (AlreadyExistsException e) {
            // Return error response for email already exists
            return ApiTrait.errorMessage(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            // Return error response for validation errors
            return ApiTrait.errorMessage(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Return error response for incorrect old password
            return ApiTrait.errorMessage(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Return error response for unexpected errors
            return ApiTrait.errorMessage(null, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*|--------------------------------------------------------------------------
                                    | End Implement
    |-------------------------------------------------------------------------- */






























}

