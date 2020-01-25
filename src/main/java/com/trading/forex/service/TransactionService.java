package com.trading.forex.service;

import com.trading.forex.entity.Account;
import com.trading.forex.entity.Recipient;
import com.trading.forex.entity.TransactionDetails;
import com.trading.forex.payload.CurrencyConverterResponse;
import com.trading.forex.payload.ExchangeRatesResponse;
import com.trading.forex.payload.PagedResponse;

import java.math.BigDecimal;
import java.util.Date;

public interface TransactionService {

    TransactionDetails getTransactionById(Long id);

    PagedResponse<TransactionDetails> findTransactionList(String username, int page, int size);

    PagedResponse<TransactionDetails> findTransactionListByAccountNumberBetweenDates(String accountNum, Date startDate, Date endDate, int page, int size);

    PagedResponse<TransactionDetails> findTransactionListByRecipientNameBetweenDates(String recipientName, Date startDate, Date endDate, int page, int size);

    void saveAccountDepositTransaction(TransactionDetails transactionDetails) throws Exception;

    void saveAccountWithdrawTransaction(TransactionDetails transactionDetails) throws Exception;

    PagedResponse<Recipient> findRecipientList(String username, int page, int size);

    Recipient saveRecipient(Recipient recipient);

    Recipient findRecipientByName(String recipientName);

    void deleteRecipientByName(String recipientName);

    ExchangeRatesResponse getAllRates(String currencyType);

    BigDecimal getConversionRate(String sourceCurrency, String destinationCurrency);

    Long transferRecipient(Recipient recipient, String currencyType, double amount, Account account) throws Exception;
}
