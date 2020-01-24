package com.trading.forex.util;

import com.trading.forex.entity.User;
import com.trading.forex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.inject.Named;

@Named
public class UtilityClass {

    @Autowired
    private UserRepository userRepository;

    public User getUserFromUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email : " + username)
        );
    }
}
