package com.agriguardian.dto;

import com.agriguardian.entity.ZoneSchedulingRule;
import com.agriguardian.enums.SchedulePeriod;
import lombok.*;

import javax.validation.constraints.Pattern;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePeriodDto {
    private Long startsToWorkAtMillis;
    private SchedulePeriod schedulePeriod;
    private String timeZone;
    private DayOfWeek dayStart;
    private DayOfWeek dayEnd;
    @Pattern(regexp = "([012])[0-9]:[0-5][0-9]:[0-5][0-9]")
    private String timeStart;
    @Pattern(regexp = "([012])[0-9]:[0-5][0-9]:[0-5][0-9]")
    private String timeEnd;

    public ZoneSchedulingRule createSchedulingRule(){
        ZoneSchedulingRule zoneSchedulingRule = new ZoneSchedulingRule();
        zoneSchedulingRule.setSchedulePeriod(schedulePeriod);
        zoneSchedulingRule.setDayStart(dayStart);
        zoneSchedulingRule.setDayEnd(dayEnd);
        zoneSchedulingRule.setTimeZone(timeZone);
        zoneSchedulingRule.setTimeStart(LocalTime.parse(timeStart));
        zoneSchedulingRule.setTimeEnd(LocalTime.parse(timeEnd));
        zoneSchedulingRule.setRuleStartsToWork(startsToWorkAtMillis);

        return zoneSchedulingRule;
    }

    public static SchedulePeriodDto of(ZoneSchedulingRule zoneSchedulingRule){
        SchedulePeriodDto schedulePeriodDto = new SchedulePeriodDto();
        schedulePeriodDto.setSchedulePeriod(Optional.ofNullable(zoneSchedulingRule.getSchedulePeriod()).orElse(null));
        schedulePeriodDto.setDayStart(Optional.ofNullable(zoneSchedulingRule.getDayStart()).orElse(null));
        schedulePeriodDto.setDayEnd(Optional.ofNullable(zoneSchedulingRule.getDayEnd()).orElse(null));
        schedulePeriodDto.setTimeZone(Optional.ofNullable(zoneSchedulingRule.getTimeZone()).orElse(null));
        schedulePeriodDto.setTimeStart(Optional.of(zoneSchedulingRule.getTimeStart().toString()).orElse(null));
        schedulePeriodDto.setTimeEnd(Optional.of(zoneSchedulingRule.getTimeEnd().toString()).orElse(null));
        schedulePeriodDto.setStartsToWorkAtMillis(Optional.ofNullable(zoneSchedulingRule.getRuleStartsToWork()).orElse(null));

        return schedulePeriodDto;
    }
}
