package com.payment.module.repository;

import com.payment.module.model.TbPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PaymentService {
    @Autowired PaymentRepository repo;

    public void save(TbPayment payment) {
        repo.save(payment);
    }

    public TbPayment get(Integer id) {
        return repo.findById(id).get();
    }
}