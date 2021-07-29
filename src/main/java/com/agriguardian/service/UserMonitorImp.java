package com.agriguardian.service;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.Border;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.service.interfaces.UserMonitor;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserMonitorImp implements UserMonitor {


    @Override
    public List<AlertGeoZone> monitor(AppUser user, Point point) {
        List<AlertGeoZone> allGeoZones = user.extractAlertGeoZones();

        //todo refactor when multiple zone types (e.g. circle and so on) will be available
        allGeoZones = allGeoZones.stream().filter(zone -> Figure.POLYGON == zone.getFigureType()).collect(Collectors.toList());

        return allGeoZones.stream()
                .filter(zone -> {
                    boolean isLocationInside = isLocationInsideZone(zone, point);
                    return isZoneViolated(zone, isLocationInside);
                }).collect(Collectors.toList());
    }


    private boolean isZoneViolated(AlertGeoZone zone, boolean hasUserInside) {
        ZoneRule zoneRule = zone.getRule();
        return hasUserInside ? ZoneRule.KEEP_OUT == zoneRule : ZoneRule.KEEP_IN == zoneRule;
    }

    private boolean isLocationInsideZone(AlertGeoZone zone, Point point) {
        GeometryFactory gf = new GeometryFactory();

        Polygon jtsPolygon = buildJtsPolygon(zone.getBorders(), gf);
        org.locationtech.jts.geom.Point jtsPoint = gf.createPoint(new Coordinate(point.getLon(), point.getLat()));
        return jtsPoint.within(jtsPolygon);
    }

    private Polygon buildJtsPolygon(List<Border> areaBorders, GeometryFactory gf) {
        Collections.sort(areaBorders);
        closeFigureIfOpen(areaBorders);
        Coordinate[] polygonJTSCoordinates = bordersToJtsCoordinates(areaBorders);
        LinearRing jtsLinerRing = new LinearRing(new CoordinateArraySequence(polygonJTSCoordinates), gf);
        return gf.createPolygon(jtsLinerRing, null);
    }

    private void closeFigureIfOpen(List<Border> bordes) {
        if (!bordes.get(0).equals(bordes.get(bordes.size() - 1))) {
            bordes.add(bordes.get(0));
        }
    }

    private Coordinate[] bordersToJtsCoordinates(List<Border> borders) {
        return borders.stream().map(b -> new Coordinate(b.getLon(), b.getLat())).toArray(Coordinate[]::new);
    }
}