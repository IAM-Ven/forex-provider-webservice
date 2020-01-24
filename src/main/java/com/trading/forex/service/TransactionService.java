package com.trading.forex.service;

import com.trading.forex.entity.Account;
import com.trading.forex.entity.Recipient;
import com.trading.forex.entity.TransactionDetails;
import com.trading.forex.payload.PagedResponse;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;

public interface TransactionService {

    PagedResponse<TransactionDetails> findTransactionList(String username, int page, int size);

    PagedResponse<TransactionDetails> findTransactionListByAccountNumberBetweenDates(String accountNum, Date startDate, Date endDate, int page, int size);

    PagedResponse<TransactionDetails> findTransactionListByRecipientNameBetweenDates(String recipientName, Date startDate, Date endDate, int page, int size);

    void saveAccountDepositTransaction(TransactionDetails transactionDetails) throws Exception;

    void saveAccountWithdrawTransaction(TransactionDetails transactionDetails) throws Exception;

    List<Recipient> findRecipientList(Principal principal);

    Recipient saveRecipient(Recipient recipient);

    Recipient findRecipientByName(String recipientName);

    void deleteRecipientByName(String recipientName);

    BigDecimal getConversionRate(String sourceCurrency, String destinationCurrency);

    void transferRecipient(Recipient recipient, String currencyType, double amount, Account account) throws Exception;
}
