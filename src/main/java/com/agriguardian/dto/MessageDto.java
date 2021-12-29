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
    private String event;
    private String groupId;
    private String userId;
    private String userFullName;
    private String violatedZoneId;
    private String violatedZoneName;
    private String time;
    private String lon;
    private String lat;
    private String violatedZoneRule;
}
