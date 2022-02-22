package com.agriguardian.dto.appUser;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.LocationData;
import com.agriguardian.entity.UserInfo;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddUserDeviceDto {
    @NotBlank(message = "field 'macAddress' is mandatory")

    private String macAddress;
    @NotBlank(message = "field 'name' is mandatory")
    private String name;
    @NotNull(message = "field 'teamGroups' is mandatory")
    private Set<Long> teamGroups;
    private Integer userAvatar;


    public AppUser buildUser() {
        return AppUser.builder()
                .username(macAddress.trim())
                .macAddress(macAddress)
                .build();
    }

    public UserInfo buildUserInfo() {
        return UserInfo.builder()
                .name(name)
                .userAvatar(userAvatar)
                .build();
    }

    public LocationData buildLocationData() {
        return LocationData.builder().lastOnline(0l).build();
    }
}
