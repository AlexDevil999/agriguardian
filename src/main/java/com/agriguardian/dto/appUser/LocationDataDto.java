package com.agriguardian.dto.appUser;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.LocationData;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;

@Builder
@Getter
public class LocationDataDto {

    private Double lon;
    private Double lat;

    private Long lastOnline;

    public static LocationDataDto of(LocationData locationData){
        return LocationDataDto.builder().lon(locationData.getLon()).lat(locationData.getLat()).lastOnline(locationData.getLastOnline()).build();
    }
}
