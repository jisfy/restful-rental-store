package com.chompchompfig.store.infrastructure.jpa;

import com.chompchompfig.store.domain.Rental;
import com.chompchompfig.store.domain.RentalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A Repository of Rentals, as per DDD
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, RentalId> {

    List<Rental> findAll();

}
