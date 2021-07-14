package com.agriguardian.dto.appUser;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.CreditCard;
import com.agriguardian.entity.UserInfo;
import com.agriguardian.util.ValidationString;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class AddUserMasterDto {
    @NotBlank(message = "field 'username/email' is mandatory")
    private String username;
    @NotNull(message = "field 'password' may not be null")
    private String password;

    @NotBlank(message = "field 'name' is mandatory")
    private String name;

    @NotBlank(message = "field 'country' is mandatory")
    private String country;
    @NotBlank(message = "field 'county' is mandatory")
    private String county;
    @NotBlank(message = "field 'city' is mandatory")
    private String city;
    @NotBlank(message = "field 'street' is mandatory")
    private String street;
    @NotBlank(message = "field 'zipAreaCode' is mandatory")
    private String zipAreaCode;
    @NotBlank(message = "field 'phoneCode' is mandatory")
    private String phoneCode;
    @NotBlank(message = "field 'phoneNumber' is mandatory")
    private String phoneNumber;
    private Boolean withTeamGroup;
    private String creditCard;


    public AppUser buildUser() {
        return AppUser.builder()
                .username(username)
                .password(password)
                .build();
    }

    public CreditCard buildCreditCard() {
        return ValidationString.isNotBlank(creditCard) ? CreditCard.builder().number(creditCard).build() : null;
    }

    public UserInfo buildUserInfo() {
        return UserInfo.builder()
                .name(name)
                .country(country)
                .county(county)
                .city(city)
                .street(street)
                .zipAreaCode(zipAreaCode)
                .phoneCode(phoneCode)
                .phoneNumber(phoneNumber)
                .build();
    }
}
