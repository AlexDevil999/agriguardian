package com.agriguardian.repository;

import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserBluetoothZoneRepository extends CrudRepository<AppUserBluetoothZone, Long> {
    void deleteByAlertBluetoothZoneId(Long alertBluetoothZoneId);

}
