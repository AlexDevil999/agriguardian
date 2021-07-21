package com.agriguardian.domain;

import com.agriguardian.entity.Border;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    private double lon;
    private double lat;


    public static Point of(Border border) {
        return Point.builder()
                .lat(border.getLat())
                .lon(border.getLon())
                .build();
    }
}
