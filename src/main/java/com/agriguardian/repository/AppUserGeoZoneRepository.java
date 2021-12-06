package com.agriguardian.repository;

import com.agriguardian.entity.manyToMany.AppUserGeoZone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserGeoZoneRepository extends CrudRepository<AppUserGeoZone, Long> {
    void deleteAppUserGeoZoneByAlertGeoZoneId(Long id);
}
