package com.agriguardian.entity;
import com.agriguardian.enums.SchedulePeriod;
import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "zone_scheduling_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneSchedulingRule {
    @Id
    @SequenceGenerator(name = "zoneSchedulingRuleSequence", sequenceName = "zone_scheduling_rule_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zoneSchedulingRuleSequence")
    private long id;

    @ManyToOne
    @JoinColumn(name = "alert_geo_zone_id")
    private AlertGeoZone alertGeoZone;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayStart;
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayEnd;

    private LocalTime timeStart;
    private LocalTime timeEnd;

    private String timeZone;

    @Enumerated(EnumType.STRING)
    private SchedulePeriod schedulePeriod;
}
