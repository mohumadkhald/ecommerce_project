package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.UserRepo;
import jdk.jshell.spi.ExecutionControl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public User findUserById(Long id) throws UserException;
    public User findUserProfileByJwt(String jwt) throws UserException;
}
