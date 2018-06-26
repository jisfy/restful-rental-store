package com.chompchompfig.store.application;

import com.chompchompfig.store.domain.*;
import com.chompchompfig.store.infrastructure.jpa.CustomerRepository;
import com.chompchompfig.store.infrastructure.jpa.FilmRepository;
import com.chompchompfig.store.infrastructure.jpa.PaymentRepository;
import com.chompchompfig.store.infrastructure.jpa.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Rental Service supporting the functionality of returning rentals, calculating surcharges, creating Payments,
 * rewarding Customers with Bonus Points, etc. All basic rental functionality lives here
 */
@Service
public class VideoRentalService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Creates a new Rental for a Customer, renting a list of Films, for a given duration in days.
     * @param customerId <p>the identifier of the Customer for whom we will create the Rental</p>
     * @param days <p>the expected duration of the Rental in days</p>
     * @param filmIds <p>a List of Film identifiers indicating the Films the Customer wants to rent</p>
     * @return <p>a new Rental for a List of Films and a Customer</p>
     * @throws IllegalArgumentException <p>in case any of the requested Films is unavailable</p>
     */
    @Transactional
    public Rental newRental(Long customerId, int days, List<Long> filmIds) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        List<Film> films = findAllFilmsByIds(filmIds);
        Rental rental = customer.get().rent(days, films);
        customerRepository.saveAndFlush(customer.get());
        return rental;
    }

    /**
     * Updates an existing Rental with a list of Films, for a given duration in days. The list of Films provided
     * will completely replace the existing one
     * @param rentalId <p>the identifier of the Rental we would like to update</p>
     * @param days <p>the expected duration of the Rental in days</p>
     * @param filmIds <p>a List of Film identifiers indicating the Films the Customer wants to rent</p>
     * @return <p>a new Rental for a List of Films and a Customer</p>
     * @throws IllegalArgumentException <p>in case any of the requested Films is unavailable</p>
     * @throws IllegalStateException <p>in case the Rental can't be updated because it is in a invalid state. Only
     * RentalStatus.AWAITING_PAYMENT supports modifications</p>
     */
    @Transactional
    public Rental updateRental(RentalId rentalId, int days, List<Long> filmIds) {
        Rental rental= rentalRepository.findById(rentalId).get();
        List<Film> films = findAllFilmsByIds(filmIds);
        rental.modify(days, films);
        Rental modifiedRental = rentalRepository.saveAndFlush(rental);
        return modifiedRental;
    }

    /**
     * Returns all the Films in a Rental for other Customers to rent
     * @param rentalId <p>the RentalId of the Rental whose Films we would like to return to the store</p>
     * @throws IllegalStateException <p>in case the return can't be performed because the given Rental is not in the
     * RentalStatus.PAID state, which is mandatory for this operation</p>
     */
    public Rental returnRental(RentalId rentalId) {
        Rental rental = rentalRepository.findById(rentalId).get();
        try {
            rental.returnAll();
        } catch (IllegalStateException ise) {
            rentalRepository.saveAndFlush(rental);
            throw ise;
        }
        Rental returnedRental = rentalRepository.saveAndFlush(rental);
        return returnedRental;
    }

    /**
     * Performs a Payment. Payment operations are idempotent. This is, a Payment which is already in PaymentStatus.DONE
     * status will not affect the Payment status any more. Performing a Payment will trigger a state change in its
     * associated Rental. If the Rental was in RentalStatus.AWAITING_PAYMENT it will transition into the
     * RentalStatus.PAID state. Therefore, accepting no longer modifications. If on the other hand the Rental was in
     * RentalStatus.AWAITING_PAYMENT_OVERDUE, which means the Customer tried to return the Films, but past the original
     * duration that was first charged. When on this state, the Rental will transition into the
     * RentalStatus.RETURNED state, and all its associated Films will be in fact returned. Also, the Customer owner of
     * the Rental will be granted with the corresponding Bonus points, which will be added to his personal
     * bonus points card.
     *
     * @param paymentId <p>the PaymentId of the Payment to perform</p>
     * @throws IllegalArgumentException <p>In case the Payment is in PaymentStatus.PENDING, but the associated Rental
     * is in an invalid state. This is, any state other than RentalStatus.AWAITING_PAYMENT or
     * RentalStatus.AWAITING_PAYMENT_OVERDUE</p>
     */
    @Transactional
    public Payment performPayment(PaymentId paymentId) {
        Payment payment = paymentRepository.findById(paymentId).get();
        payment.perform();
        Payment paymentPerformed = paymentRepository.saveAndFlush(payment);
        return paymentPerformed;
    }

    /**
     * Finds all the Films given a list of their identifiers
     * @param filmIds <p>the list of Film identifiers to find</p>
     * @return <p>a List of Films with the given identifiers</p>
     */
    private List<Film> findAllFilmsByIds(List<Long> filmIds) {
        Iterable<Film> filmsIterable = filmRepository.findAllById(filmIds);
        return toFilmList(filmsIterable);
    }

    /**
     * Turns an Iterable of Films into a List of Films
     * @param filmsIterable <p>the Iterable to convert</p>
     * @return <p>a List of Films converted from the given Iterable</p>
     */
    private List<Film> toFilmList(Iterable<Film> filmsIterable) {
        List<Film> films = new ArrayList<>();
        filmsIterable.iterator().forEachRemaining(films::add);
        return films;
    }
}
