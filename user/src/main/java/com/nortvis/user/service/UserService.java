package com.nortvis.user.service;

import com.nortvis.user.entity.User;
import com.nortvis.user.repository.UserRepository;
import com.nortvis.user.request.CreateUserRequest;
import com.nortvis.user.request.LoginRequest;
import com.nortvis.user.response.CreateUserResponse;
import com.nortvis.user.response.UserResponse;
import com.nortvis.user.utility.BcryptUtil;
import com.nortvis.user.utility.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private BcryptUtil bcryptUtil;

    public CreateUserResponse createUser(CreateUserRequest request) {
        // Check if a user with the same username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return new CreateUserResponse(401, "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new CreateUserResponse(401, "Email already exists");
        }

        if (request.getPassword() != null) {
            request.setPassword(bcryptUtil.hashPassword(request.getPassword()));
        }

        User user = new User(request);
        userRepository.save(user);

        return new CreateUserResponse(200, "Registration successful");
    }

    public UserResponse getme() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        UserResponse userResponse=new UserResponse(user);
        return userResponse;
    }
    public String generateToken(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());

        if (user != null) {
            System.out.printf("user found");
            if (bcryptUtil.isPasswordValid(request.getPassword(),user.getPassword())) {
                return jwtUtil.generateToken(user.getUsername());
            }
        }
        return null;
    }
}