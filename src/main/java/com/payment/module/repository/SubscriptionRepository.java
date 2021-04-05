package com.payment.module.repository;

import com.payment.module.model.TbSubscription;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<TbSubscription, Integer> {

}