package com.projects.ecommerce.service;

import com.projects.ecommerce.config.JwtProvider;
import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userrepo;
    private final JwtProvider jwtProvider;

    @Override
    public User findUserById(Long id) throws UserException {
        Optional<User> user = userrepo.findById(id);
        if (user.isPresent())
        {
            return user.get();
        }
        throw new  UserException("User Not Found");
        // Implementation to find a user by email
        // You need to provide the logic to search for the user in your data source
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {
        String email = jwtProvider.getEmailFromToken(jwt);
        User user = userrepo.findByEmail(email);

        if (user == null)
        {
            throw new UserException("user not found");
        }
        // Implementation to find a user's profile by JWT token
        // You need to provide the logic to retrieve the user's profile based on the JWT token
        return user; // Placeholder return value
    }
}
