package com.agriguardian.dto.appUser;

import com.agriguardian.enums.GroupRole;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupBindDto {
    private Long groupId;
    private String groupName;
    private Long userId;
    private GroupRole groupRole;
}