package com.agriguardian.dto.appUser;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.UserInfo;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
public class EditUserFollowerDto {
    @NotNull(message = "field 'id' is mandatory")
    private Long id;
    @NotBlank(message = "field 'NewUsername' is mandatory")
    private String newUsername;

    private String newPassword;

    @NotBlank(message = "field 'name' is mandatory")
    private String name;
    @NotNull(message = "field 'userAvatar' is mandatory")
    private Integer userAvatar;


    public AppUser buildUser() {
        return AppUser.builder()
                .username(newUsername.toLowerCase().trim())
                .password(newPassword)
                .build();
    }

    public UserInfo buildUserInfo() {
        return UserInfo.builder()
                .name(name)
                .userAvatar(userAvatar)
                .build();
    }
}
