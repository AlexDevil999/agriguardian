package com.agriguardian.dto;

import com.agriguardian.entity.*;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.*;

@Builder
@Getter
public class ResponseUserDto {
    private long id;
    private String username;
    private String name;
    private SubscriptionDto subscription;
    private Long ownerOfGroup;
    //todo fix
    private Set<Long> groups = new HashSet<>(Arrays.asList(1l, 5l));

    private long createdOnMs;

    private Status status;
    private UserRole userRole;

    private UserInfoDto userInfo;


    public static ResponseUserDto of(AppUser u) {
//        u.getAppUserTeamGroups().forEach(tg -> groups.add(tg.getId()));
        return ResponseUserDto.builder()
        .id(u.getId())
        .username(u.getUsername())
        .name(u.getName())
        .subscription(SubscriptionDto.of(u.getSubscription()))
//        .ownerOfGroup(u.getTeamGroup().getId())//todo delete hardcode
        .ownerOfGroup(5l)
                .groups(new HashSet<>(Arrays.asList(1l, 5l)))
        .createdOnMs(u.getCreatedOnMs())
        .status(u.getStatus())
        .userRole(u.getUserRole())
        .userInfo(UserInfoDto.of(u.getUserInfo()))
                .build();
    }
}
