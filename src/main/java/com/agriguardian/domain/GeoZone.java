package com.agriguardian.domain;

import com.agriguardian.enums.Figure;

import java.util.List;

public class GeoZone {
    private Figure figureType;
    private Point center;
    private Integer radius;
    private List<Point> points;
}
