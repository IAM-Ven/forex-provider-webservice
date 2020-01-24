package com.trading.forex.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class TransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date date;
    private String description;
    private String status;
    private double amount;
    private BigDecimal availableBalance;
    private String type;
    private BigDecimal conversionMultiple;
    @Column(name = "conversion_from")
    private String sourceType;
    @Column(name = "conversion_to")
    private String destinationType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private Recipient recipient;

    public TransactionDetails(Date date, String description,String type, String status, double amount, BigDecimal availableBalance, BigDecimal conversionMultiple, String sourceType, String destinationType, Account account) {
        this.date = date;
        this.description = description;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.availableBalance = availableBalance;
        this.conversionMultiple = conversionMultiple;
        this.sourceType = sourceType;
        this.destinationType = destinationType;
        this.account = account;
    }

    public TransactionDetails() {
    }
}
