package com.agriguardian.dto;

import com.agriguardian.entity.UserInfo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoDto {
    private String name;
    private String county;
    private String city;
    private String street;
    private String zipAreaCode;
    private String phoneCode;
    private String phoneNumber;


    public static UserInfoDto of(UserInfo ui) {
        return ui == null ? null :
                UserInfoDto.builder()
                        .name(ui.getName())
        .county(ui.getCountry())
        .city(ui.getCity())
        .street(ui.getStreet())
        .zipAreaCode(ui.getZipAreaCode())
        .phoneCode(ui.getPhoneCode())
        .phoneNumber(ui.getPhoneNumber())
                .build();
    }
}
