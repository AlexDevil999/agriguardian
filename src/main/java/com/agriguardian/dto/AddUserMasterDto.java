package com.agriguardian.dto;

import com.agriguardian.entity.CreditCard;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.UserInfo;
import com.agriguardian.util.ValidationString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddUserMasterDto {
    @NotBlank(message = "field 'username' may not be empty")
    private String username;
    @NotNull(message = "field 'password' may not be null or empty")
    private String password;

    @NotBlank(message = "field 'name' may not be empty")
    private String name;

    @NotBlank(message = "field 'county' may not be empty")
    private String county;
    @NotBlank(message = "field 'city' may not be empty")
    private String city;
    @NotBlank(message = "field 'street' may not be empty")
    private String street;
    @NotBlank(message = "field 'zipCode' may not be empty")
    private String zipCode;
    @NotBlank(message = "field 'areaCode' may not be empty")
    private String areaCode;
    @NotBlank(message = "field 'phoneCode' may not be empty")
    private String phoneCode;
    @NotBlank(message = "field 'phoneNumber' may not be empty")
    private String phoneNumber;
    private Boolean withTeamGroup;


    private String creditCard;


    public AppUser buildUser() {
        return AppUser.builder()
                .username(username)
                .password(password)
                .name(name)
                .build();
    }

    public CreditCard buildCreditCard() {
        return ValidationString.isNotBlank(creditCard) ? CreditCard.builder().number(creditCard).build() : null;
    }

    public UserInfo buildAddress() {
        return UserInfo.builder()
                .county(county)
                .city(city)
                .street(street)
                .zipCode(zipCode)
                .areaCode(areaCode)
                .phoneCode(phoneCode)
                .phoneNumber(phoneNumber)
                .build();
    }
}
