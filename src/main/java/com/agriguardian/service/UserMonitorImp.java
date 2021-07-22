package com.agriguardian.service;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.manyToMany.AppUserGeoZone;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.service.interfaces.UserMonitor;
import lombok.AllArgsConstructor;
import org.springframework.data.geo.Polygon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserMonitorImp implements UserMonitor {


    @Override
    public List<AlertGeoZone> monitor(AppUser user, Point point) {
//        Polygon p = new Polygon();
//        p.
//
//        user.getAppUserGeoZones().parallelStream()
//                .map(AppUserGeoZone::getAlertGeoZone)
//                .filter(zone -> zone.)
//
//        user.getAppUserTeamGroups().parallelStream()
//                .map(AppUserTeamGroup::getTeamGroup)
//                .map(T)
        return null;
    }


}
