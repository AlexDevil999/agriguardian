package com.agriguardian.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationDto {
    private Long userId;
    private List<Long> violatedZones;
}
