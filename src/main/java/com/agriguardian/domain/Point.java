package com.agriguardian.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    private double lon;
    private double lat;
}
