package com.payment.module.repository;

import com.payment.module.model.TbPlan;
import org.springframework.data.repository.CrudRepository;

public interface PlanRepository extends CrudRepository<TbPlan, Integer> {

}