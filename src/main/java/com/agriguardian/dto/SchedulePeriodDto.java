package com.agriguardian.dto;

import com.agriguardian.entity.ZoneSchedulingRule;
import com.agriguardian.enums.SchedulePeriod;
import lombok.*;

import javax.validation.constraints.Pattern;
import java.time.DayOfWeek;
import java.time.LocalTime;

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
        schedulePeriodDto.setSchedulePeriod(zoneSchedulingRule.getSchedulePeriod());
        schedulePeriodDto.setDayStart(zoneSchedulingRule.getDayStart());
        schedulePeriodDto.setDayEnd(zoneSchedulingRule.getDayEnd());
        schedulePeriodDto.setTimeZone(zoneSchedulingRule.getTimeZone());
        schedulePeriodDto.setTimeStart(zoneSchedulingRule.getTimeStart().toString());
        schedulePeriodDto.setTimeEnd(zoneSchedulingRule.getTimeEnd().toString());
        schedulePeriodDto.setStartsToWorkAtMillis(zoneSchedulingRule.getRuleStartsToWork());

        return schedulePeriodDto;
    }
}
