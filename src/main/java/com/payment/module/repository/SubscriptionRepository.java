package com.payment.module.repository;

import com.payment.module.model.TbSubscription;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface SubscriptionRepository extends CrudRepository<TbSubscription, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE TB_SUBSCRIPTION u set SUB_ACTIVE = '0' where u.ADV_ID = :userId",
            nativeQuery = true)
    void updateSubscription(@Param("userId") String userId);
}