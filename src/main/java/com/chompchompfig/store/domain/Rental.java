package com.chompchompfig.store.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A Rental order comprising several Films which will be rented for a given duration. Rentals have a status
 * {@link RentalStatus} which affects which operations can be performed on it. Rentals also belong to a Customer who
 * owns it. Rental is an Aggregate comprising RentalItems
 */
@Entity
public class Rental {

    public static final String RENTAL_CREATION_FAILURE_MESSAGE =
            "Can't create a rental with unavailable Films. Please check Film ids :";
    public static final String RENTAL_UPDATE_FAILURE_MESSAGE =
            "Can't update a Rental which is not in AWAITING_PAYMENT state";
    public static final String RETURN_OVERDUE_RENTAL_FAILURE_MESSAGE =
            "Can't return Films from overdue rental with pending charges. Please check the Rental status " +
                    "again, and make sure to perform the corresponding payments";

    @EmbeddedId
    private RentalId id;

    @ManyToOne(optional=false)
    @JoinColumn(name="CUSTOMER_ID",referencedColumnName="ID", insertable = false, updatable = false)
    private Customer customer;
    @OneToMany(mappedBy="rental",targetEntity=RentalItem.class, fetch=FetchType.LAZY,  cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RentalItem> items;
    private int days;
    private Date rentalStartDate;
    private RentalStatus status;
    @OneToMany(mappedBy="rental",targetEntity=Payment.class, fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Payment> payments;
    @Transient
    private RentalPriceCalculatorService rentalPriceCalculatorService;
    @Transient
    private BonusPointsCalculatorService bonusPointsCalculatorService;


    public Rental() {
        this.status = RentalStatus.AWAITING_PAYMENT;
        this.items = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.rentalPriceCalculatorService = new CategoryBasedRentalPriceCalculatorService();
        this.bonusPointsCalculatorService = new SimpleBonusPointsCalculatorService();
    }

    public Rental(Customer customer, int days, List<Film> films) {
        this();
        Film.validateFilmsAvailability(films, this::buildIllegalArgumentException);
        this.setId(new RentalId(customer.getId(), System.nanoTime()));
        this.customer = customer;
        this.days = days;
        this.items = toRentalItemList(films);
        this.payments.add(getPayment());
    }

    /**
     * Gets the unique identifier of a Rental
     * @return <p>the Rental unique identifier</p>
     */
    public RentalId getId() {
        return id;
    }


    /**
     * Sets the unique identifier of a Rental
     * @param id <p>the unique identifier to use</p>
     */
    public void setId(RentalId id) {
        this.id = id;
    }

    /**
     * Gets the Rental associated Customer
     * @return <p>the associated Customer</p>
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Gets the Items comprising the Rental. This is, the Films rented
     * @return <p>the list of Items comprising the Rental</p>
     */
    public List<RentalItem> getItems() {
        return this.items;
    }


    /**
     * Gets the Rental duration in days
     * @return <p>the Rental duration in days</p>
     */
    public int getDays() {
        return days;
    }

    /**
     * The Rental start date. This is, the initial Date to start calculating the Rental duration
     * @return <p>the Rental start date</p>
     */
    public Date getRentalStartDate() {
        return rentalStartDate;
    }

    /**
     * Gets the Rental Status
     * @return <p>the Rental status</p>
     */
    public RentalStatus getStatus() {
        return status;
    }

    /**
     * Gets the Payments associated with a Rental
     * @return <p>the list of Payments of a Rental</p>
     */
    public List<Payment> getPayments() {
        return payments;
    }

    /**
     * Sets the Rental duration in days
     * @param days <p>the number of days for the Rental</p>
     */
    void setDays(int days) {
        this.days = days;
    }

    /**
     * Sets the Rental start date. This is, the initial Date to start calculating the Rental duration
     * @param rentalStartDate <p>the start date of the Rental</p>
     */
    void setRentalStartDate(Date rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    /**
     * Sets the Rental Status
     * @return <p>the Rental status to set</p>
     */
    void setStatus(RentalStatus status) {
        this.status = status;
    }

    /**
     * Gets the overdue status of a Rental. This is, if the Rental duration was past
     * @return <p>the overdue Rental status</p>
     */
    public boolean isOverdue() {
        return isOverdue(LocalDate.now());
    }

    /**
     * Gets the overdue status of a Rental. This is, if the Rental duration was past using the
     * given argument as the Rental start date
     * @param currentDate <p>the Rental start date used to calculate the overdue status</p>
     */
    public boolean isOverdue(LocalDate currentDate) {
        return (getEffectiveDaysRented(currentDate) > days);
    }

    /**
     * Checks if the Rental has PENDING Payments
     * @return <ul><li>True, if the Rental has PENDING Payments</li><li>False otherwise</li></ul>
     */
    public boolean hasPendingPayments() {
        return payments.stream().anyMatch(p -> p.getStatus().equals(PaymentStatus.PENDING));
    }

    /**
     * Gets the effective days rented given a current date. Calculates the number of days a Rental has lasted
     * from its start date til the given one
     * @param currentDate <p>the current date (when is considered now)</p>
     * @return <p>number of effective days a Rental has lasted if it ended on the current date given</p>
     */
    int getEffectiveDaysRented(LocalDate currentDate) {
        LocalDate startDate = rentalStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int effectiveDaysRented = (int) ChronoUnit.DAYS.between(startDate, currentDate);
        return effectiveDaysRented;
    }

    /**
     * Gets the effective days rented. Calculates the number of days a Rental has lasted from its start date til now
     * @return <p>number of effective days a Rental has lasted if it ended now</p>
     */
    public int getEffectiveDaysRented() {
        LocalDate currentDate = LocalDate.now();
        return getEffectiveDaysRented(currentDate);
    }

    public void cancel() {
    }

    /**
     * Updates an existing Rental with a list of Films, for a given duration in days. The list of Films provided
     * will completely replace the existing one
     * @param days <p>the expected duration of the Rental in days</p>
     * @param films <p>a List of Films the Customer wants to rent</p>
     * @return <p>a new Rental for a List of Films and a Customer</p>
     * @throws IllegalArgumentException <p>in case any of the requested Films is unavailable</p>
     * @throws IllegalStateException <p>in case the Rental can't be updated because it is in a invalid state. Only
     * RentalStatus.AWAITING_PAYMENT supports modifications</p>
     */
    public void modify(int days, List<Film> films) {
        validateRentalStatusForUpdate();
        Film.validateFilmsAvailability(films, this::buildIllegalArgumentException);
        setDays(days);
        replaceAllRentalItemsInRental(films);
        updatePendingPaymentPriceForRental();
    }

    /**
     * Returns all the Films in a Rental for other Customers to rent
     * @throws IllegalStateException <p>in case the return can't be performed because the given Rental is not in the
     * RentalStatus.PAID state, which is mandatory for this operation</p>
     */
    public void returnAll() {
        if (getStatus().equals(RentalStatus.PAID)) {
            handleReturnInPaidStatus();
        } else {
            handleReturnInInvalidStatus();
        }
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
     * @param paymentId <p>the PaymentID of the Payment to perform</p>
     * @throws IllegalArgumentException <p>In case the Payment is in PaymentStatus.PENDING, but the associated Rental
     * is in an invalid state. This is, any state other than RentalStatus.AWAITING_PAYMENT or
     * RentalStatus.AWAITING_PAYMENT_OVERDUE</p>
     */
    public void pay(PaymentId paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.perform();
    }

    /**
     * Updates the bonus points in the Bonus Points Card of a Customer according to the given Rental
     * Gets the bonus points that should be rewarded for this rental
     */
    public long getBonusPoints() {
        long bonusPointsForRental = bonusPointsCalculatorService.getBonusPointsForRental(this);
        return bonusPointsForRental;
    }

    /**
     * Reward the associated Customer, who created this Rental with the corresponding Bonus Points
     */
    void rewardBonusPoints() {
        getCustomer().updateBonusPointsForRental(getBonusPoints());
    }

    private Payment getPaymentById(final PaymentId paymentId) {
        Optional<Payment> paymentWithId = getPayments().stream().filter(p -> p.getId().equals(paymentId)).findFirst();
        return paymentWithId.get();
    }

    /**
     * Handles a Rental return operation when it is not in a valid state. This is, when the Rental is not in the
     * RentalStatus.PAID state
     * @throws IllegalStateException <p>in case the return can't be performed because the given Rental is not in the
     * RentalStatus.PAID state, which is mandatory for this operation</p>
     */
    void handleReturnInInvalidStatus() {
        final String returnRentalFailureMessage = "Can't return Films from a Rental in a status other than " +
                RentalStatus.PAID;
        throw new IllegalStateException(returnRentalFailureMessage);
    }


    /**
     * Handles a Rental return operation when it is in a valid state. This is, when the Rental is in the
     * RentalStatus.PAID state. Even then, the Rental could be overdue. This happens if the Rental was not returned
     * on time, and it has financial consequences. A new Payment is added to the Rental in that case. If the Rental
     * is not overdue, all its Films are made available again immediately. The Rental status is changed to
     * RentalStatus.RETURNED
     * @throws IllegalStateException <p>if the Rental is overdue</p>
     */
    void handleReturnInPaidStatus() {
        if (isOverdue()) {
            Payment surchargePayment = getPaymentForSurcharge();
            getPayments().add(surchargePayment);
            setStatus(RentalStatus.AWAITING_PAYMENT_OVERDUE);
            throw new IllegalStateException(RETURN_OVERDUE_RENTAL_FAILURE_MESSAGE);
        } else {
            markAllFilmsAsAvailable();
            setStatus(RentalStatus.RETURNED);
        }
    }

    /**
     * Creates a Surcharge Payment for a Rental. The Payment will reflect the extra amount that needs to be charged to
     * the Customer for the additional days he has rented the Films.
     * @return <p>a Payment with the total surcharge amount of the given Rental</p>
     */
    public Payment getPaymentForSurcharge() {
        long rentalSurchargePrice = rentalPriceCalculatorService.getSurchargePrice(this);
        return getPaymentForRentalWithPrice(rentalSurchargePrice);
    }

    /**
     * Gets the List of Films that are part of a Rental
     * @return <p>the List of Films in a Rental</p>
     */
    List<Film> getFilms() {
        return getItems().stream().map(i -> i.getFilm()).collect(Collectors.toList());
    }

    /**
     * Marks all Films in a Rental as available
     */
    void markAllFilmsAsAvailable() {
        List<Film> films = getFilms();
        films.stream().forEach(f -> f.setAvailable(true));
    }

    /**
     * Updates a Rental PENDING Payment, reflecting changes in price, consequence of changes in the selection of
     * Films
     */
    void updatePendingPaymentPriceForRental() {
        Payment pendingPayment = getPendingPayment();
        long updatedRentalPrice = rentalPriceCalculatorService.getPrice(this);
        pendingPayment.setAmount(updatedRentalPrice);
    }

    /**
     * Gets a PENDING Payment from a Rental
     * @return <p>a PENDING Payment from a Rental</p>
     * @throws NoSuchElementException <p>in case the Payment could not be found</p>
     */
    Payment getPendingPayment() {
        Optional<Payment> payment =
                getPayments().stream().filter(p -> p.getStatus().equals(PaymentStatus.PENDING)).findFirst();
        return payment.get();
    }

    /**
     * Validates a Rental in order to perform an Update operation. Rentals need to be in RentalStatus.AWAITING_PAYMENT
     * state in order to be suitable for updating
     * @throws IllegalStateException <p>in case the validation fails</p>
     */
    void validateRentalStatusForUpdate() {
        if (!getStatus().equals(RentalStatus.AWAITING_PAYMENT)) {
            throw new IllegalStateException(RENTAL_UPDATE_FAILURE_MESSAGE);
        }
    }

    /**
     * Replaces all Rental Items in a Rental, with new ones corresponding to the new selection of Films
     * @param films <p>the new selection of Films used to replace the old Rental Items</p>
     */
    private void replaceAllRentalItemsInRental(List<Film> films) {
        List<RentalItem> rentalItems = toRentalItemList(films);
        removeAllRentalItems();
        getItems().addAll(rentalItems);
    }

    /**
     * Removes all Rental Items from a Rental
     */
    void removeAllRentalItems() {
        IntStream.range(0, getItems().size()).forEach(idx -> getItems().remove(0));
    }

    /**
     * Creates a List of Rental Items representing the rental of a List of Films
     * @param films <p>the list of Films to use to build the Rental Items</p>
     * @return <p>a List of RentalItems reflecting the rental of the given Films</p>
     */
    private List<RentalItem> toRentalItemList(List<Film> films) {
        List<RentalItem> rentalItems = films.stream().map(f -> RentalItem.from(f, this)).collect(Collectors.toList());
        return rentalItems;
    }

    /**
     * Creates a Payment for a Rental. The Payment will reflect the amount charged to the Customer for all the
     * Films included in it
     * @return <p>a Payment with the total amount of the given Rental</p>
     */
    private Payment getPayment() {
        long rentalPrice = rentalPriceCalculatorService.getPrice(this);
        return getPaymentForRentalWithPrice(rentalPrice);
    }

    private Payment getPaymentForRentalWithPrice(long rentalPrice) {
        Payment payment = new Payment();
        PaymentId paymentId = new PaymentId(getId().getCustomerId(), getId().getId(), System.nanoTime());
        payment.setId(paymentId);
        payment.setRental(this);
        payment.setAmount(rentalPrice);
        payment.setDate(new Date());
        payment.setCurrency(rentalPriceCalculatorService.getPriceCurrency().toString());
        return payment;
    }

    /**
     * Builds IllegalArgumentExceptions with a message indicating that a new Rental can't be created due to
     * unavailable Films
     * @param unavailableIdsMessage <p>a suffix message containing the identifiers of unavailable Films</p>
     * @return <p>an IllegalArgumentException with a Rental creation failure message</p>
     */
    RuntimeException buildIllegalArgumentException(String unavailableIdsMessage) {
        String failureMessage = RENTAL_CREATION_FAILURE_MESSAGE + unavailableIdsMessage;
        return new IllegalArgumentException(failureMessage);
    }

}
