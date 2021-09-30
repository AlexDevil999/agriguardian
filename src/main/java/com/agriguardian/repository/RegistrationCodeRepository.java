package com.agriguardian.repository;

import com.agriguardian.entity.RegistrationCode;
import org.springframework.data.repository.CrudRepository;

public interface RegistrationCodeRepository extends CrudRepository<RegistrationCode,Long> {
    void deleteByOwnerId(Long id);
}
