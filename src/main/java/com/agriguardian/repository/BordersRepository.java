package com.agriguardian.repository;

import com.agriguardian.entity.Border;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BordersRepository extends CrudRepository<Border, Long> {
}
