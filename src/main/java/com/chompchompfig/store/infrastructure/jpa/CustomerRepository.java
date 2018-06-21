package com.chompchompfig.store.infrastructure.jpa;

import com.chompchompfig.store.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A Repository of Customers, as per DDD
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findAll();

}
