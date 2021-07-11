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
    private String county;
    private String city;
    private String street;
    @Column(name = "zip_code")
    private String zipCode;
    @Column(name = "area_code")
    private String areaCode;
    @Column(name = "phone_code")
    private String phoneCode;
    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;


    public void addAppUser(AppUser user) {
        user.addUserInfo(this);
    }
}
