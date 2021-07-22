package com.agriguardian.controller;

import com.agriguardian.dto.LocationDto;
import com.agriguardian.dto.ViolationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {


    //for storing of history
    @PostMapping("/report")
    public ViolationDto notifyLocation(@Valid @RequestBody List<LocationDto> locations, Errors errors, Principal principal) {
        Collections.sort(locations);
//
//        Topol

        return ViolationDto.builder().userId(1L).violatedZones(Arrays.asList(1L)).build();
    }


    public static void main(String[] args) {
        List<Coordinate> points = new ArrayList<Coordinate>();
        points.add(new Coordinate(50.459743, 30.3602));
        points.add(new Coordinate(50.458760, 30.415131));
        points.add(new Coordinate(50.447720, 30.47693));
        points.add(new Coordinate(50.426291, 30.369126));
        points.add(new Coordinate(50.459743, 30.3602));

        GeometryFactory gf = new GeometryFactory();
        Polygon polygon = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()])), gf), null);
        Point point = gf.createPoint(new Coordinate(50.457776, 30.437619));

        System.out.println("----------------");
        System.out.println(point.within(polygon));
    }
}
