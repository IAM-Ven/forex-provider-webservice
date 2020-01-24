package com.trading.forex.controller;

import com.trading.forex.entity.User;
import com.trading.forex.exception.ResourceNotFoundException;
import com.trading.forex.repository.UserRepository;
import com.trading.forex.security.CurrentUser;
import com.trading.forex.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return userRepository.getOne(currentUser.getId());
    }

    @GetMapping("/checkUsernameAvailability")
    public Boolean checkUsernameAvailability(@RequestParam("username") String username){
        boolean isUsernameAvailable = !userRepository.existsByUsername(username);
        return isUsernameAvailable;
    }

    @GetMapping("/checkEmailAvailability")
    public Boolean checkEmailAvailability(@RequestParam("email") String email){
        boolean isEmailAvailable = !userRepository.existsByEmail(email);
        return isEmailAvailable;
    }

    @GetMapping("/user/{username}")
    public User getUserByUsername(@PathVariable("username") String username) {
        return  userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

}
