package com.payment.module.repository;

import com.payment.module.model.TbPlan;
import com.payment.module.model.TbSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class SubscriptionService {
    @Autowired
    SubscriptionRepository repo;

    public void save(TbSubscription payment) {
        repo.save(payment);
    }

    public TbSubscription get(Integer id) {
        return repo.findById(id).get();
    }
}
