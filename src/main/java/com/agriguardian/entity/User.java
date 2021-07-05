package com.agriguardian.entity;

import com.agriguardian.enums.Status;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @SequenceGenerator(name = "usersSequence", sequenceName = "users_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersSequence")
    private long id;
    private String username;
    private String password;
    private String otp;

    private String firstName;
    private String lastName;
    private String phone;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CreditCard card;

    private long createdOnMs;
    private long updatedOnMs;
    private long otpCreatedOnMs;

    private String guardianInviteCode;
    private String vulnerableInviteCode;

    @Enumerated(EnumType.STRING)
    private Status status;
}
