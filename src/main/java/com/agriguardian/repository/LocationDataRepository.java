package com.agriguardian.repository;

import com.agriguardian.entity.LocationData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationDataRepository extends JpaRepository<LocationData,Long> {
}
