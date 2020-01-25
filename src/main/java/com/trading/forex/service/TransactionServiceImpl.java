package com.trading.forex.service;

import com.trading.forex.entity.Account;
import com.trading.forex.entity.Recipient;
import com.trading.forex.entity.TransactionDetails;
import com.trading.forex.entity.User;
import com.trading.forex.exception.AppException;
import com.trading.forex.payload.CurrencyConverterResponse;
import com.trading.forex.payload.ExchangeRatesResponse;
import com.trading.forex.payload.PagedResponse;
import com.trading.forex.repository.AccountRepository;
import com.trading.forex.repository.RecipientRepository;
import com.trading.forex.repository.TransactionRepository;
import com.trading.forex.repository.UserRepository;
import com.trading.forex.util.UtilityClass;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    @Value("${api.exchange.rate}")
    String exchangeRateApi;

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

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public PagedResponse<TransactionDetails> findTransactionList(String username, int page, int size) {
        Pageable pageable = utilities.validatePageNumberAndSize(page, size);
        Page<TransactionDetails> transactions = transactionRepository.findAll(pageable);
        User user = utilities.getUserFromUsername(username);
        if (transactions.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), transactions.getNumber(),
                    transactions.getSize(), transactions.getTotalElements(), transactions.getTotalPages(), transactions.isLast());
        }

        return new PagedResponse<>(user.getAccount().getTransactionDetailsList(), transactions.getNumber(),
                transactions.getSize(), transactions.getTotalElements(), transactions.getTotalPages(), transactions.isLast());
    }

    @Override
    public PagedResponse<TransactionDetails> findTransactionListByAccountNumberBetweenDates(String accountNum, Date startDate, Date endDate, int page, int size) {
        Pageable pageable = utilities.validatePageNumberAndSize(page, size);
        Page<TransactionDetails> transactions = transactionRepository.findAllByAccountNumberBetweenDates(accountNum, startDate, endDate, pageable);

        return new PagedResponse<>((transactions.getNumberOfElements() == 0) ? Collections.emptyList() : transactions.getContent(), transactions.getNumber(),
                transactions.getSize(), transactions.getTotalElements(), transactions.getTotalPages(), transactions.isLast());
    }

    @Override
    public PagedResponse<TransactionDetails> findTransactionListByRecipientNameBetweenDates(String recipientName, Date startDate, Date endDate, int page, int size) {
        utilities.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<TransactionDetails> transactions = transactionRepository.findAllByRecipientNameBetweenDates(recipientName, startDate, endDate, pageable);

        return new PagedResponse<>((transactions.getNumberOfElements() == 0) ? Collections.emptyList() : transactions.getContent(), transactions.getNumber(),
                transactions.getSize(), transactions.getTotalElements(), transactions.getTotalPages(), transactions.isLast());
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
        } catch (Exception ex) {
            log.info("Failure withdrawing with reason {}", ex.getMessage());
        }
    }

    @Override
    public PagedResponse<Recipient> findRecipientList(String username, int page, int size) {
        Pageable pageable = utilities.validatePageNumberAndSize(page, size);
        Page<Recipient> recipients = recipientRepository.findAllByUsername(username, pageable);

        return new PagedResponse<>((recipients.getNumberOfElements() == 0) ? Collections.emptyList() : recipients.getContent(), recipients.getNumber(),
                recipients.getSize(), recipients.getTotalElements(), recipients.getTotalPages(), recipients.isLast());
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
        ResponseEntity<CurrencyConverterResponse> response = null;
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(exchangeRateApi)
                    .queryParam("base", sourceCurrency)
                    .queryParam("symbols", destinationCurrency);
            response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    null, CurrencyConverterResponse.class);
        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage());
        }
        return new BigDecimal(response.getBody().getRates().get(destinationCurrency));
    }

    private Boolean isDaysLessThanOrEqualToThree(LocalDate start, LocalDate end) {
        return ((Days.daysBetween(start, end).isLessThan(Days.THREE)) && (Days.daysBetween(start, end).equals(Days.THREE)));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Long transferRecipient(Recipient recipient, String currencyType, double amount, Account account) throws Exception {
        Date date = null;
        TransactionDetails transactionDetails = null;
        BigDecimal conversionRate = getConversionRate(currencyType, recipient.getCurrencyType());
        try {
            TransactionDetails details = transactionRepository.findByRecipientName(recipient.getName());
            if (isDaysLessThanOrEqualToThree(new LocalDate(details.getDate().getTime()), new LocalDate())) {
                throw new AppException("Transfer cannot be made for " + recipient.getName() + ". Please try after " + (Days.daysBetween(new LocalDate(details.getDate().getTime()), new LocalDate())) + " days");
            }
            account.setAccountBalance(account.getAccountBalance().subtract((new BigDecimal(amount)).multiply(conversionRate)));
            accountRepository.save(account);
            date = new Date();
            transactionDetails = new TransactionDetails(date, "Successfully transferred to " + recipient.getName() + " with amount " + amount, "Transfer", "Success", amount, account.getAccountBalance(), conversionRate, currencyType, recipient.getCurrencyType(), account);
            transactionRepository.save(transactionDetails);
        } catch (Exception ex) {
            date = new Date();
            transactionDetails = new TransactionDetails(date, "Failure transferring to " + recipient.getName() + " with amount " + amount, "Transfer", "Failure", amount, account.getAccountBalance(), conversionRate, currencyType, recipient.getCurrencyType(), account);
            transactionRepository.save(transactionDetails);
            log.info("Transaction Failure with stacktrace {}", ex.getMessage());
        }
        return transactionDetails.getId();
    }

    @Override
    public ExchangeRatesResponse getAllRates(String currencyType) {
        ExchangeRatesResponse ratesResponse = new ExchangeRatesResponse();
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(exchangeRateApi)
                    .queryParam("base", currencyType);
            ResponseEntity<CurrencyConverterResponse> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    null, CurrencyConverterResponse.class);

            CurrencyConverterResponse currencyConverter = response.getBody();
            Map<String, BigDecimal> rates = new HashMap<>();
            ratesResponse.setDate(currencyConverter.getDate());
            currencyConverter.getRates().keySet().forEach(key -> {
                BigDecimal val = new BigDecimal(currencyConverter.getRates().get(key));
                rates.put(key, val);
            });
            ratesResponse.setRates(rates);

        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage());
        }
        return ratesResponse;
    }

    @Override
    public TransactionDetails getTransactionById(Long id) {
        return transactionRepository.getOne(id);
    }
}
