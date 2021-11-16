package com.agriguardian.dto.appUser;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.UserInfo;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
public class EditUserFollowerSelfDto {
    @NotBlank(message = "field 'username' is mandatory")
    private String oldUsername;
    @NotNull(message = "field 'password' may not be null")
    private String oldPassword;
    @NotBlank(message = "field 'name' is mandatory")
    private String name;
    @NotBlank(message = "field 'userAvatar' is mandatory")
    private Integer userAvatar;


    public AppUser buildUser() {
        return AppUser.builder()
                .username(oldUsername.toLowerCase().trim())
                .password(oldPassword)
                .build();
    }

    public UserInfo buildUserInfo() {
        return UserInfo.builder()
                .name(name)
                .userAvatar(userAvatar)
                .build();
    }
}
