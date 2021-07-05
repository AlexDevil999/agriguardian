package com.agriguardian.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@Builder
public class Address {
    @Id
    @SequenceGenerator(name = "addressesSequence", sequenceName = "addresses_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressesSequence")
    private long id;
//    private String city;
//    private String street;
//    private String number;
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
