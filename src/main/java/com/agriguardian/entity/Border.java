package com.agriguardian.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table(name = "borders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Border implements Comparable{
    @Id
    @SequenceGenerator(name = "bordersSequence", sequenceName = "borders_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bordersSequence")
    private long id;
    private Double lon;
    private Double lat;
    private Integer order;
    @ManyToOne
    @JoinColumn(name = "alert_geo_zone_id")
    private AlertGeoZone alertGeoZone;


    public void addAlertGeoZone(AlertGeoZone zone) {
        zone.addBorders(Arrays.asList(this));
    }


    @Override
    public int compareTo(Object o) {
        return order - ((Border) o).order;
    }
}
