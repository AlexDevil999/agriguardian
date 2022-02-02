package com.agriguardian.repository;

import com.agriguardian.entity.AlertGeoZone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertGeoZoneRepository extends CrudRepository<AlertGeoZone, Long> {

}
