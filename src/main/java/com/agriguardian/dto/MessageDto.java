package com.agriguardian.dto;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.EventType;
import lombok.*;

import javax.validation.constraints.NotNull;

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
    private Long time;
    private Point point;
    private String violatedZoneRule;
}
