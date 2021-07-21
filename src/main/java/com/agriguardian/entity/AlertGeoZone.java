package com.agriguardian.entity;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.manyToMany.AppUserGeoZone;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "geo_zones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AlertGeoZone {
    @Id
    @SequenceGenerator(name = "alertGeoZoneSequence", sequenceName = "geo_zones_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alertGeoZoneSequence")
    private long id;

    @Enumerated(EnumType.STRING)
    private ZoneRule rule;

    @ManyToOne
    @JoinColumn(name = "team_group_id")
    private TeamGroup teamGroup;

    @OneToMany(mappedBy = "alertGeoZone",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<AppUserGeoZone> appUserGeoZones;

        @Enumerated(EnumType.STRING)
        @Column(name = "figure")
    private Figure figureType;
    @Column(name = "center_lon")
    private Double centerLon;
    @Column(name = "center_lat")
    private Double centerLat;
    private Integer radius;

    @OneToMany(mappedBy = "alertGeoZone",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Border> borders;

    public void addTeamGroup(TeamGroup teamGroup) {
        teamGroup.addAlertGeoZone(this);
    }

    public AppUserGeoZone addVulnerable(AppUser user) {
        if (appUserGeoZones == null) appUserGeoZones = new HashSet<>();

        final AppUserGeoZone newUserZone =
                AppUserGeoZone.builder()
                        .appUser(user)
                        .alertGeoZone(this)
                        .build();
        Optional<AppUserGeoZone> fromSet = appUserGeoZones.stream()
                .filter(userZone -> userZone.equals(newUserZone))
                .findAny();

        if (fromSet.isPresent()) {
            return fromSet.get();
        } else {
            appUserGeoZones.add(newUserZone);
            user.getAppUserGeoZones().add(newUserZone); //todo check this
            return newUserZone;
        }
    }

    public Set<AppUser> extractVulnerables() {
        if (appUserGeoZones == null) return new HashSet<>();
        return appUserGeoZones.stream().map(AppUserGeoZone::getAppUser).collect(Collectors.toSet());
    }

    public void addBorder(Border border) {
        if (this.borders == null) this.borders = new ArrayList<>();
            border.setAlertGeoZone(this);
            borders.add(border);

            //todo add increasing of # (order) of each border afet this (f.e. this is #3, 3->4, 4->5 and so on)
    }

    public void addBorders(List<Border> borders) {
        if (this.borders == null) this.borders = new ArrayList<>();
        borders.forEach(border -> {
            border.setAlertGeoZone(this);
            borders.add(border);
        });
    }

    public List<Border> bordersByPoints(List<Point> points) {
        if (borders == null) borders = new ArrayList<>();
        int[] index = {0};

        points.forEach(point -> {
            Border border = Border.builder()
                    .alertGeoZone(this)
                    .lat(point.getLat())
                    .lon(point.getLon())
                    .order(index[0]++)
                    .build();
            borders.add(border);
        });
        return borders;
    }
}


