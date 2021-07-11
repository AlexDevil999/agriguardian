package com.agriguardian.dto;

import com.agriguardian.entity.UserInfo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoDto {
    private String county;
    private String city;
    private String street;
    private String zipCode;
    private String areaCode;
    private String phoneCode;
    private String phoneNumber;


    public static UserInfoDto of(UserInfo ui) {
        return ui == null ? null :
                UserInfoDto.builder()
        .county(ui.getCounty())
        .city(ui.getCity())
        .street(ui.getStreet())
        .zipCode(ui.getZipCode())
        .areaCode(ui.getAreaCode())
        .phoneCode(ui.getPhoneCode())
        .phoneNumber(ui.getPhoneNumber())
                .build();
    }
}