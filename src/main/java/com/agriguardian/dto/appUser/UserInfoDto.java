package com.agriguardian.dto.appUser;

import com.agriguardian.entity.UserInfo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoDto {
    private String name;
    private String country;
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
                        .country(ui.getCountry())
                        .county(ui.getCounty())
                        .city(ui.getCity())
                        .street(ui.getStreet())
                        .zipAreaCode(ui.getZipAreaCode())
                        .phoneCode(ui.getPhoneCode())
                        .phoneNumber(ui.getPhoneNumber())
                        .build();
    }

}
