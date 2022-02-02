package com.agriguardian.repository;

import com.agriguardian.entity.AlertBluetoothZone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertBluetoothZoneRepository extends CrudRepository<AlertBluetoothZone, Long> {

}
