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

    @NotBlank(message = "field 'name' is mandatory")
    private String name;
    @NotNull(message = "field 'userAvatar' is mandatory")
    private Integer userAvatar;


    public AppUser buildUser(String password, String username) {
        return AppUser.builder()
                .username(username.toLowerCase().trim())
                .password(password)
                .build();
    }

    public UserInfo buildUserInfo() {
        return UserInfo.builder()
                .name(name)
                .userAvatar(userAvatar)
                .build();
    }
}
