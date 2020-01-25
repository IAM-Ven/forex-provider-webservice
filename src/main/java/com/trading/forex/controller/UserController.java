package com.trading.forex.controller;

import com.trading.forex.entity.Recipient;
import com.trading.forex.entity.TransactionDetails;
import com.trading.forex.entity.User;
import com.trading.forex.exception.ResourceNotFoundException;
import com.trading.forex.payload.ApiResponse;
import com.trading.forex.payload.PagedResponse;
import com.trading.forex.repository.UserRepository;
import com.trading.forex.security.CurrentUser;
import com.trading.forex.security.UserPrincipal;
import com.trading.forex.service.TransactionService;
import com.trading.forex.util.Constants;
import com.trading.forex.util.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/forex")
@Slf4j
public class UserController {

    @Inject
    UtilityClass utilities;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return userRepository.getOne(currentUser.getId());
    }

    @GetMapping("/checkUsernameAvailability")
    public Boolean checkUsernameAvailability(@RequestParam("username") String username) {
        boolean isUsernameAvailable = !userRepository.existsByUsername(username);
        return isUsernameAvailable;
    }

    @GetMapping("/checkEmailAvailability")
    public Boolean checkEmailAvailability(@RequestParam("email") String email) {
        boolean isEmailAvailable = !userRepository.existsByEmail(email);
        return isEmailAvailable;
    }

    @GetMapping("/users/{username}")
    public User getUserByUsername(@PathVariable("username") String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @GetMapping("/user/recipients")
    public PagedResponse<Recipient> getAllRecipients(@CurrentUser UserPrincipal principal,
                                                     @RequestParam(name = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                     @RequestParam(name = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return transactionService.findRecipientList(principal.getUsername(), page, size);
    }

    @PostMapping("/recipient/add")
    public ResponseEntity<ApiResponse> addRecipient(@RequestBody Recipient recipient, @CurrentUser UserPrincipal principal) {
        User user = utilities.getUserByUsername(recipient.getUser().getUsername());
        recipient.setUser(user);
        transactionService.saveRecipient(recipient);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/recipients")
                .buildAndExpand().toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Recipient " + recipient.getName() + " added"));
    }


    @PutMapping("/recipient/{recipientName}")
    public ResponseEntity<ApiResponse> editRecipient(@PathVariable("recipientName") String recipientName, @CurrentUser UserPrincipal principal) {

        Recipient recipient = transactionService.findRecipientByName(recipientName);

        User user = userRepository.findByUsername(recipient.getUser().getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", recipient.getUser().getUsername()));

        recipient.setUser(user);
        transactionService.saveRecipient(recipient);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/user/recipients")
                .buildAndExpand().toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Recipient " + recipientName + " edited successfully"));
    }

    @DeleteMapping("/recipient/{recipientName}")
    public ResponseEntity<ApiResponse> deleteRecipient(@PathVariable("recipientName") String recipientName, @CurrentUser UserPrincipal principal) {

        transactionService.deleteRecipientByName(recipientName);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/user/recipients")
                .buildAndExpand().toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Recipient " + recipientName + " deleted successfully"));
    }

    @GetMapping("/transaction/{transactionId}")
    public TransactionDetails getTransactionById(@PathVariable("transactionId") Long id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/transfer/{recipientName}/{amount}")
    @Transactional
    public ResponseEntity<ApiResponse> transferRecipient(@PathVariable("recipientName") String recipientName, @PathVariable("amount") double amount, @CurrentUser UserPrincipal principal) throws Exception {
        User user = utilities.getUserByUsername(principal.getUsername());
        Recipient recipient = transactionService.findRecipientByName(recipientName);
        Long id = transactionService.transferRecipient(recipient, recipient.getCurrencyType(), amount, user.getAccount());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/forex/transaction/{transactionId}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Recipient " + recipientName + " deleted successfully"));
    }

}
