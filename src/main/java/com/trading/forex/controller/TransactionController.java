package com.trading.forex.controller;

import com.trading.forex.entity.Recipient;
import com.trading.forex.entity.TransactionDetails;
import com.trading.forex.entity.User;
import com.trading.forex.payload.ApiResponse;
import com.trading.forex.payload.ExchangeRatesResponse;
import com.trading.forex.payload.PagedResponse;
import com.trading.forex.repository.UserRepository;
import com.trading.forex.security.CurrentUser;
import com.trading.forex.security.UserPrincipal;
import com.trading.forex.service.AccountService;
import com.trading.forex.service.TransactionService;
import com.trading.forex.util.Constants;
import com.trading.forex.util.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.util.Date;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Inject
    UtilityClass utilities;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    @GetMapping
    public PagedResponse<TransactionDetails> getTransactions(@CurrentUser UserPrincipal currentUser,
                                                             @RequestParam(name = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                             @RequestParam(name = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return transactionService.findTransactionList(currentUser.getUsername(), page, size);
    }

    @GetMapping("/conversionRates/{currencyType}")
    public ResponseEntity<ExchangeRatesResponse> getRates(@PathVariable("currencyType") String currencyType) {
        return new ResponseEntity<>(transactionService.getAllRates(currencyType), HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    public TransactionDetails getTransactionById(@PathVariable("transactionId") Long id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/transfer/{recipientName}")
    @Transactional
    public ResponseEntity<ApiResponse> transferRecipient(@PathVariable("recipientName") String recipientName,
                                                         @RequestParam("amount") double amount,
                                                         @CurrentUser UserPrincipal principal) throws Exception {
        User user = utilities.getUserByUsername(principal.getUsername());
        Recipient recipient = transactionService.findRecipientByName(recipientName);
        Long id = transactionService.transferRecipient(recipient, new Date(), recipient.getCurrencyType(), amount, user.getAccount());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/transaction/{transactionId}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Recipient " + recipientName + " deleted successfully"));
    }

    @PostMapping("/transfer/{recipientName}/{date}")
    @Transactional
    public ResponseEntity<ApiResponse> scheduleTransferRecipient(@PathVariable("recipientName") String recipientName,
                                                                 @RequestParam("amount") double amount,
                                                                 @PathVariable("date") Date date,
                                                                 @CurrentUser UserPrincipal principal) throws Exception {
        User user = utilities.getUserByUsername(principal.getUsername());
        Recipient recipient = transactionService.findRecipientByName(recipientName);
        Long id = transactionService.transferRecipient(recipient, date, recipient.getCurrencyType(), amount, user.getAccount());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/transaction/{transactionId}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Recipient " + recipientName + " deleted successfully"));
    }

}
