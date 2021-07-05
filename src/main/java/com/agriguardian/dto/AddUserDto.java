package com.agriguardian.dto;

import com.agriguardian.entity.UserInfo;
import com.agriguardian.entity.CreditCard;
import com.agriguardian.entity.User;
import com.agriguardian.util.ValidationString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddUserDto {
    @NotBlank(message = "field 'username' may not be empty")
    private String username;
    @NotNull(message = "field 'password' may not be null or empty")
    private String password;

    @NotBlank(message = "field 'firstName' may not be empty")
    private String firstName;
    @NotBlank(message = "field 'lastName' may not be empty")
    private String lastName;
    @NotBlank(message = "field 'phone' may not be empty")
    private String phone;
//    @NotBlank(message = "field 'address' may not be empty")
//    private String city;
//    @NotBlank(message = "field 'street' may not be empty")
//    private String street;
//    @NotBlank(message = "field 'number' may not be empty")
//    private String number;
    @NotBlank(message = "field 'address' may not be empty")
    private String address;
    private String creditCard;


    public User buildUser() {
        return User.builder()
                .username(username)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .build();
    }

    public CreditCard buildCreditCard() {
        return ValidationString.isNotBlank(creditCard) ? CreditCard.builder().number(creditCard).build() : null;
    }

    public UserInfo buildAddress() {
        return UserInfo.builder()
//                .city(city)
//                .street(street)
//                .number(number)
                .address(address)
                .build();
    }

}
