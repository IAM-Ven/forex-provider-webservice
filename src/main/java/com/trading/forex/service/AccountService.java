package com.trading.forex.service;

import com.trading.forex.entity.Account;

import java.security.Principal;

public interface AccountService {

    Account createAccount(String currencyType) throws Exception;

    void deposit(String currencyType, double amount, Principal principal) throws Exception;

}
