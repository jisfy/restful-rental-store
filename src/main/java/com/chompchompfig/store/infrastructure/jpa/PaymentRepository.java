package com.chompchompfig.store.infrastructure.jpa;

import com.chompchompfig.store.domain.Payment;
import com.chompchompfig.store.domain.PaymentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A Repository of Payments, as per DDD
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, PaymentId> {

    List<Payment> findAll();

}
