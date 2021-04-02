package com.payment.module.repository;

import com.payment.module.model.TbPayment;
import org.springframework.data.repository.CrudRepository;


public interface PaymentRepository extends CrudRepository<TbPayment, Integer> {

}