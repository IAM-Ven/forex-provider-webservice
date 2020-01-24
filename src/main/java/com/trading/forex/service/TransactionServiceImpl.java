package com.trading.forex.service;

import com.trading.forex.entity.Account;
import com.trading.forex.entity.Recipient;
import com.trading.forex.entity.TransactionDetails;
import com.trading.forex.entity.User;
import com.trading.forex.repository.AccountRepository;
import com.trading.forex.repository.RecipientRepository;
import com.trading.forex.repository.TransactionRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);


    @Inject
    private UtilityClass utilities;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @Override
    public List<TransactionDetails> findTransactionList(String username) {
        User user = utilities.getUserFromUsername(username);
        List<TransactionDetails> transactionDetailsList = user.getAccount().getTransactionDetailsList();
        return transactionDetailsList;
    }

    @Override
    public List<TransactionDetails> findTransactionListByAccountNumberBetweenDates(String accountNum, Date startDate, Date endDate) {
        return transactionRepository.findAllByAccountNumberBetweenDates(accountNum, startDate, endDate);
    }

    @Override
    public List<TransactionDetails> findTransactionListByRecipientNameBetweenDates(String recipientName, Date startDate, Date endDate) {
        return transactionRepository.findAllByRecipientNameBetweenDates(recipientName, startDate, endDate);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveAccountDepositTransaction(TransactionDetails transactionDetails) throws Exception {
        try {
            transactionRepository.save(transactionDetails);
        } catch (Exception ex) {
            log.info("Failure Depositing with reason {}", ex.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveAccountWithdrawTransaction(TransactionDetails transactionDetails) throws Exception {
        try {
            transactionRepository.save(transactionDetails);
        } catch (Exception ex){
            log.info("Failure withdrawing with reason {}", ex.getMessage());
        }
    }

    @Override
    public List<Recipient> findRecipientList(Principal principal) {
        String username = principal.getName();
        List<Recipient> recipientList = recipientRepository.findAll().stream()
                .filter(recipient -> username.equals(recipient.getUser().getUsername()))
                .collect(Collectors.toList());

        return recipientList;
    }

    @Override
    public Recipient saveRecipient(Recipient recipient) {
        return recipientRepository.save(recipient);
    }

    @Override
    public Recipient findRecipientByName(String recipientName) {
        return recipientRepository.findByName(recipientName);
    }

    @Override
    public void deleteRecipientByName(String recipientName) {
        recipientRepository.deleteByName(recipientName);
    }

    @Override
    public BigDecimal getConversionRate(String sourceCurrency, String destinationCurrency) {
        return null;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void transferRecipient(Recipient recipient, String currencyType, double amount, Account account) throws Exception {
        Date date = null;
        BigDecimal conversionRate = getConversionRate(currencyType, recipient.getCurrencyType());
        try {
            account.setAccountBalance(account.getAccountBalance().subtract((new BigDecimal(amount)).multiply(conversionRate)));
            accountRepository.save(account);
            date = new Date();
            TransactionDetails transactionDetails = new TransactionDetails(date, "Successfully transferred to " + recipient.getName() + " with amount " + amount, "Transfer", "Success", amount, account.getAccountBalance(), conversionRate, currencyType, recipient.getCurrencyType(), account);
            transactionRepository.save(transactionDetails);
        } catch (Exception ex) {
            date = new Date();
            TransactionDetails transactionDetails = new TransactionDetails(date, "Failure transferring to " + recipient.getName() + " with amount " + amount, "Transfer", "Failure", amount, account.getAccountBalance(), conversionRate, currencyType, recipient.getCurrencyType(), account);
            transactionRepository.save(transactionDetails);
            log.info("Transaction Failure with stacktrace {}", ex.getMessage());
        }

    }
}
