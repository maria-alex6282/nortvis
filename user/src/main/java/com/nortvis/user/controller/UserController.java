package com.nortvis.user.controller;
import com.nortvis.user.request.CreateUserRequest;
import com.nortvis.user.request.LoginRequest;
import com.nortvis.user.response.CreateUserResponse;
import com.nortvis.user.response.LoginResponse;
import com.nortvis.user.response.UserResponse;
import com.nortvis.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private  UserService userService;
    @PostMapping("/register")
    public CreateUserResponse createUser(@RequestBody CreateUserRequest request) {
    return userService.createUser(request);
    }

    @GetMapping("/me")
    public UserResponse getMe() {
        return userService.getme();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String token = userService.generateToken(loginRequest);
        if (token != null) {
            return new LoginResponse(token, "Login successful");
        } else {
            return new LoginResponse(null, "Login failed");
        }
    }
}
