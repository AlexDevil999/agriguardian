package com.agriguardian.repository;

import com.agriguardian.entity.ZoneSchedulingRule;
import org.springframework.data.repository.CrudRepository;

public interface ZoneSchedulingRuleRepository extends CrudRepository<ZoneSchedulingRule,Long> {
    void deleteByAlertGeoZoneId(Long id);
}
