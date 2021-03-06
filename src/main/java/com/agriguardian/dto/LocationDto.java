package com.agriguardian.dto;

import com.agriguardian.domain.Point;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocationDto implements Comparable<LocationDto> {

    @NotNull(message = "field 'time' is mandatory")
    private Long time;
    @NotNull(message = "field 'point' is mandatory")
    private Point point;


    @Override
    public int compareTo(LocationDto o) {
        return (int) (time - o.getTime());
    }
}
