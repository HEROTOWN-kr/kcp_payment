package com.payment.module.repository;

import com.payment.module.model.TbPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<TbPayment, Long> {

}
