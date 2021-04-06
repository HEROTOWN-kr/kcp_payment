package com.payment.module.service;

import com.payment.module.model.TbAdvertiser;
import com.payment.module.repository.AdvertiserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AdvertiserService {
    @Autowired
    AdvertiserRepository repo;

    public TbAdvertiser get(Integer id) {
        return repo.findById(id).get();
    }
}
