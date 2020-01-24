package com.trading.forex.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
