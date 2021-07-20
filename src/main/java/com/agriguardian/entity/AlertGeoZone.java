package com.agriguardian.entity;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.entity.manyToMany.AppUserGeoZone;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

//@Entity
//@Table(name = "geo_zones")
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(of = "id")
public class AlertGeoZone {
//    @Id
//    @SequenceGenerator(name = "alertGeoZoneSequence", sequenceName = "geo_zones_id_seq", allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alertGeoZoneSequence")
//    private long id;
//    private Long teamGroupId;
//    @Enumerated(EnumType.STRING)
//    private ZoneRule rule;
//    @ManyToOne
//    @JoinColumn(name = "team_group_id")
//    private TeamGroup teamGroup;
//
//    //todo delete
//    private List<Long> vulnerables;
//
//    @Enumerated(EnumType.STRING)
//    private Figure figureType;
//    @Column(name = "center_lon")
//    private Double centerLon;
//    @Column(name = "center_lat")
//    private Double centerLat;
//    private Integer radius;
//
//    @OneToMany(mappedBy = "alertGeoZone")
//    private Set<AppUserGeoZone> appUserGeoZones;
//
//    @OneToMany(mappedBy = "alertGeoZone")
//    private TreeSet<Border> borders;
//
//
//    public void addTeamGroup(TeamGroup teamGroup) {
//        teamGroup.addAlertGeoZone(this);
//    }
//
//    public void addBorder(List<Border> borders) {
//        borders.forEach(border -> {
//            border.setAlertGeoZone(this);
//            borders.add(border);
//        });
//    }
//
//    public Set<Border> buildBordersByPoints(List<Point> points) {
//        for (int i = 0; i < points.size(); i++) {
//            Border border = Border.builder()
//                    .alertGeoZone(this)
//                    .lat(points.get(i).getLat())
//                    .lon(points.get(i).getLon())
//                    .order(i)
//                    .build();
//            borders.add(border);
//        }
//
//        return borders;
//    }
}


