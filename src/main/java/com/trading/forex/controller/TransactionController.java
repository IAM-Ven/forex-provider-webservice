package com.trading.forex.controller;

import com.trading.forex.entity.TransactionDetails;
import com.trading.forex.payload.ExchangeRatesResponse;
import com.trading.forex.payload.PagedResponse;
import com.trading.forex.repository.UserRepository;
import com.trading.forex.security.CurrentUser;
import com.trading.forex.security.UserPrincipal;
import com.trading.forex.service.AccountService;
import com.trading.forex.service.TransactionService;
import com.trading.forex.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

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


}
