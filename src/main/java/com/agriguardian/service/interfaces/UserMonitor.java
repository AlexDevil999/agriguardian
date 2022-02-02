package com.agriguardian.service.interfaces;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;

import java.util.List;

public interface UserMonitor {
    /**
     * Monitors user's location and defines if the user is restricting any GeoZone
     *
     * @return violated geozones
     */
    List<AlertGeoZone> monitor(AppUser user, Point point);
}
