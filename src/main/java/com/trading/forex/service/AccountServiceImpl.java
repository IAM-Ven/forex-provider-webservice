package com.trading.forex.service;

import com.trading.forex.entity.Account;
import com.trading.forex.entity.TransactionDetails;
import com.trading.forex.entity.User;
import com.trading.forex.repository.AccountRepository;
import com.trading.forex.repository.UserRepository;
import com.trading.forex.util.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

@Service

public class AccountServiceImpl implements AccountService {

    private static int newAccountNum = 1211345120;

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Inject
    private UtilityClass utilities;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;


    @Override
    @Transactional(rollbackOn = Exception.class)
    public Account createAccount(String currencyType) throws Exception {
        Account account = null;
        try {
            account = new Account();
            account.setAccountBalance(new BigDecimal(0.0));
            account.setAccountNumber(Integer.toString(++newAccountNum));
            account.setCurrencyType(currencyType);

            accountRepository.save(account);
        } catch (Exception ex) {
            log.info("Could not create account");
            log.info(ex.getMessage());
        }
        return accountRepository.findByAccountNumber(account.getAccountNumber());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deposit(String currencyType, double amount, Principal principal) throws Exception {
        try {
            User user = utilities.getUserFromUsername(principal.getName());
            Account account = user.getAccount();
            account.setAccountBalance(account.getAccountBalance().add(new BigDecimal(amount)));
            account.setCurrencyType(currencyType);
            accountRepository.save(account);

            Date date = new Date();
            TransactionDetails transactionDetails = new TransactionDetails(date, "Amount " + amount + "deposited of currency type" + currencyType + " with amount " + amount, "Deposit", "Success", amount, account.getAccountBalance(), new BigDecimal(0.0), currencyType, currencyType, account);
            transactionService.saveAccountDepositTransaction(transactionDetails);
        } catch (Exception ex) {
            log.info("Deposit failed due to {}", ex.getMessage());
        }
    }

}
