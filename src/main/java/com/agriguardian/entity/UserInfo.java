package com.agriguardian.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@Builder
public class UserInfo {
    @Id
    @SequenceGenerator(name = "userInfoSequence", sequenceName = "user_info_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userInfoSequence")
    private long id;
//    private String city;
//    private String street;
//    private String number;
    private String address;
    private String phone;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
