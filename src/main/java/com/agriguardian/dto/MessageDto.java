package com.agriguardian.dto;

import com.agriguardian.entity.EventType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private EventType event;
    private Long groupId;
    private Long userId;
    private String userFullName;
    private Long violatedZoneId;
    private String violatedZoneName;
    private LocationDto location;
}
