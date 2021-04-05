package com.payment.module.repository;

import com.payment.module.model.TbPayment;
import com.payment.module.model.TbPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PlanService {
    @Autowired
    PlanRepository repo;

    public void save(TbPlan payment) {
        repo.save(payment);
    }

    public TbPlan get(Integer id) {
        return repo.findById(id).get();
    }
}
