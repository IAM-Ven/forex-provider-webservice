package com.trading.forex.repository;

import com.trading.forex.entity.TransactionDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionDetails, Long> {

    List<TransactionDetails> findAll();

    @Query("from Recipient as rt inner join TransactionDetails as td where rt.name=:recipientName")
    TransactionDetails findByRecipientName(@Param("recipientName") String recipientName);

    @Query("from Recipient as rt inner join TransactionDetails as td where rt.name=:name and td.date>= :startDate and td.date<= :endDate")
    Page<TransactionDetails> findAllByRecipientNameBetweenDates(@Param("name") String name, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query("FROM Account as acc inner join TransactionDetails as td where acc.accountNumber=:accountNum and td.date>= :startDate and td.date<= :endDate")
    Page<TransactionDetails> findAllByAccountNumberBetweenDates(@Param("accountNum") String accountNum, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
}
