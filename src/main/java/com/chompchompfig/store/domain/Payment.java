package com.chompchompfig.store.domain;

import javax.persistence.*;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

/**
 * A Payment performed typically by a Customer in order to perform for the rental of Films
 */
@Entity
public class Payment {

    public static final String PAYMENT_ASSOCIATED_RENTAL_NOT_FOUND_MESSAGE =
            "Couldn't find the associated rental for ";
    public static final String RENTAL_COMPLETION_FAILURE_MESSAGE =
            "Can't perform a payment for a rental with unavailable Films. Some Films may have been rented since the " +
                    "creation of the Rental. Please check Film ids :";

    @EmbeddedId
    private PaymentId id;

    private Long amount;
    private String currency;
    private Date date;
    private PaymentStatus status;

    @ManyToOne(optional=false)
    @JoinColumns({
            @JoinColumn(name="CUSTOMER_ID", referencedColumnName="CUSTOMER_ID", insertable = false, updatable = false),
            @JoinColumn(name="RENTAL_ID", referencedColumnName="ID", insertable = false, updatable = false),
    })
    private Rental rental;

    public Payment() {
        this.status = PaymentStatus.PENDING;
        this.date = new Date();
    }

    /**
     * Gets a Payment unique identifier
     * @return <p>the Payment unique identifier</p>
     */
    public PaymentId getId() {
        return id;
    }


    /**
     * Sets the Payment unique identifier
     * @param id <p>the unique identifier to use</p>
     */
    public void setId(PaymentId id) {
        this.id = id;
    }


    /**
     * Gets the Payment amount
     * @return <p>the Payment amount</p>
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * Gets the Payment Currency code
     * @return <p>the Payment Currency code</p>
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Gets the Payment Date (when the Payment took place)
     * @return <p>the Payment Date</p>
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the Payment associated Rental
     * @return <p>the Payment Rental</p>
     */
    public Rental getRental() {
        return rental;
    }

    /**
     * Gets the status of the Payment
     * @return <p>the Payment status</p>
     */
    public PaymentStatus getStatus() {
        return status;
    }

    /**
     * Sets the Payment amount
     * @param amount <p>the amount to set</p>
     */
    void setAmount(Long amount) {
        this.amount = amount;
    }

    /**
     * Sets the Payment Currency code
     * @param currency <p>a String holding the Payment Currency code</p>
     */
    void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Sets the Payment Date
     * @param date <p>the Payment Date to set</p>
     */
    void setDate(Date date) {
        this.date = date;
    }

    /**
     * Sets the Payment associated Rental
     * @param rental <p>the rental to associate the Payment to</p>
     */
    void setRental(Rental rental) {
        this.rental = rental;
    }

    /**
     * Sets the Payment Status
     * @param status <p>the status of the Payment</p>
     */
    void setStatus(PaymentStatus status) {
        this.status = status;
    }

    /**
     * Fulfills the Payment, setting its status to DONE
     */
    public void fulfill() {
        this.status = PaymentStatus.DONE;
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
     * @throws IllegalArgumentException <p>In case the Payment is in PaymentStatus.PENDING, but the associated Rental
     * is in an invalid state. This is, any state other than RentalStatus.AWAITING_PAYMENT or
     * RentalStatus.AWAITING_PAYMENT_OVERDUE</p>
     */
    public void perform() {
        if (!getStatus().equals(PaymentStatus.DONE)) {
            Rental rental = getRental();
            if (rental == null) {
                throw new IllegalArgumentException(PAYMENT_ASSOCIATED_RENTAL_NOT_FOUND_MESSAGE + this);
            }
            if (rental.getStatus().equals(RentalStatus.AWAITING_PAYMENT)) {
                handlePaymentForRentalInAwaitingPaymentState(rental);
            } else if (rental.getStatus().equals(RentalStatus.AWAITING_PAYMENT_OVERDUE)) {
                handlePaymentForRentalInAwaitingPaymentOverdueState(rental);
            } else {
                String rentalPaymentErrorMessage = "Can't perform for a Rental in invalid state " + rental.getStatus() +
                        ", only payments for rentals in " + RentalStatus.AWAITING_PAYMENT  + " or " +
                        RentalStatus.AWAITING_PAYMENT_OVERDUE + " are supported";
                throw new IllegalStateException(rentalPaymentErrorMessage);
            }
        }
    }

    /**
     * Handles a Payment operation for a Rental which is in AWAITING_PAYMENT state
     * @param rental <p>the Rental for which the Payment is to be handled</p>
     * @throws ConcurrentModificationException <p>in case the Payment operation can't be performed successfully.
     * Typically this happens when the Films first associated with a Rental are no longer available. Probably due
     * to concurrent changes by other Customers</p>
     */
    private void handlePaymentForRentalInAwaitingPaymentState(Rental rental) {
        List<Film> films = rental.getFilms();
        Film.validateFilmsAvailability(films, this::buildConcurrentModificationException);
        rental.rewardBonusPoints();
        rental.setRentalStartDate(new Date()); // update the rental start date when the first payment is done
        rental.setStatus(RentalStatus.PAID);
        fulfill();
        markAllFilmsAsUnAvailable(rental);
    }

    /**
     * Handles a Payment operation for a Rental which is in AWAITING_PAYMENT_OVERDUE state
     * @param rental <p>the Rental for which the Payment is to be handled</p>
     */
    private void handlePaymentForRentalInAwaitingPaymentOverdueState(Rental rental) {
        // we should return all movies here and fulfill the payment, all in one transaction
        fulfill();
        markAllFilmsAsAvailable(rental);
        rental.setStatus(RentalStatus.RETURNED);
    }

    /**
     * Marks all Films in a Rental with the given availability status
     * @param rental <p>the Rental whose Films we would like to mark</p>
     */
    void markAllFilmsAvailability(Rental rental, boolean isAvailable) {
        List<Film> films = rental.getFilms();
        films.stream().forEach(f -> f.setAvailable(isAvailable));
    }

    /**
     * Marks all Films in a Rental as unavailable
     * @param rental <p>the Rental whose Films we would like to mark as unavailable</p>
     */
    void markAllFilmsAsUnAvailable(Rental rental) {
        markAllFilmsAvailability(rental, false);
    }

    /**
     * Marks all Films in a Rental as available
     * @param rental <p>the Rental whose Films we would like to mark as available</p>
     */
    void markAllFilmsAsAvailable(Rental rental) {
        markAllFilmsAvailability(rental, true);
    }

    /**
     * Builds ConcurrentModificationException with a message indicating that a Rental can't be completed due to
     * unavailable Films. Some Films may have changed their status since the creation of the Rental
     * @param unavailableIdsMessage <p>a suffix message containing the identifiers of unavailable Films</p>
     * @return <p>an ConcurrentModificationException with a Rental completion failure message</p>
     */
    RuntimeException buildConcurrentModificationException(String unavailableIdsMessage) {
        String failureMessage = RENTAL_COMPLETION_FAILURE_MESSAGE + unavailableIdsMessage;
        return new ConcurrentModificationException(failureMessage);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", date=" + date +
                ", status=" + status +
                ", rental=" + rental +
                '}';
    }
}
