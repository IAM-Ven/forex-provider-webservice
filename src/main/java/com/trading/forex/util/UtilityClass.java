package com.trading.forex.util;

import com.trading.forex.entity.User;
import com.trading.forex.exception.BadRequestException;
import com.trading.forex.exception.ResourceNotFoundException;
import com.trading.forex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Pageable validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size > Constants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + Constants.MAX_PAGE_SIZE);
        }

        return PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

}
