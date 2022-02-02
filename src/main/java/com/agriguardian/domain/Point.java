package com.agriguardian.domain;

import com.agriguardian.entity.Border;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"lon", "lat"})
public class Point {
    @NotNull(message = "field 'lon' (longitude) is mandatory")
    private Double lon;
    @NotNull(message = "field 'lat' (latitude) is mandatory")
    private Double lat;


    public static Point of(Border border) {

        return Point.builder()
                .lat(border.getLat())
                .lon(border.getLon())
                .build();
    }
}
