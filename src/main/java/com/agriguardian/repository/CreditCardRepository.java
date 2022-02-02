package com.agriguardian.repository;

import com.agriguardian.entity.CreditCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository extends CrudRepository<CreditCard, Long> {

}
