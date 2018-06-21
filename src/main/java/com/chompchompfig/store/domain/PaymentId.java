package com.chompchompfig.store.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Value Object that acts as an identifier that helps to uniquely distinguish a Payment from others. Payments
 * belong to a Rental. Although the same Rental could have several Payments associated
 */
@Embeddable
public class PaymentId implements Serializable {

    public static final String PAYMENT_ID_PARSING_ERROR_MESSAGE = "" +
            "The supplied PaymentId must be three long numbers separated by a dot . like so [rentalId.id]";

    @Column(name="CUSTOMER_ID")
    private Long customerId;
    @Column(name="RENTAL_ID")
    private Long rentalId;
    @Column(name="ID")
    private Long id;

    protected PaymentId() {
    }

    public PaymentId(Long customerId, Long rentalId, Long id) {
        this.customerId = customerId;
        this.rentalId = rentalId;
        this.id = id;
    }

    /**
     * Gets the PaymentId CustomerId part
     * @return <p>the customerId part of a PaymentId</p>
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * Gets the PaymentId RentalId part
     * @return <p>the rentalId part of a PaymentId</p>
     */
    public Long getRentalId() {
        return rentalId;
    }

    /**
     * Gets the Id part of a PaymentId
     * @return <p>the Id part of a PaymentId</p>
     */
    public Long getId() {
        return id;
    }

    /**
     * Creates a Payment Identifier from the given Payment Id String. Typically a String with the format
     * RentalId.Timestamp
     * @param paymentId <p>the Payment Id string with the format described above</p>
     * @return <p>a PaymentId instance whose parts where parsed from the given input string</p>
     * @throws IllegalArgumentException <p>in case the input String can't be parsed successfully</p>
     */
    public static PaymentId from(String paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException(PAYMENT_ID_PARSING_ERROR_MESSAGE);
        }

        String paymentIdParts[] = paymentId.split("\\.");
        if (paymentIdParts.length != 3) {
            throw new IllegalArgumentException(PAYMENT_ID_PARSING_ERROR_MESSAGE);
        }

        try {
            String customerIdPart = paymentIdParts[0];
            String rentalIdPart = paymentIdParts[1];
            String idPart = paymentIdParts[2];
            Long customerId = Long.parseLong(customerIdPart);
            Long rentalId = Long.parseLong(rentalIdPart);
            Long id = Long.parseLong(idPart);
            return new PaymentId(customerId, rentalId, id);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(PAYMENT_ID_PARSING_ERROR_MESSAGE);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentId)) return false;
        PaymentId that = (PaymentId) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getRentalId(), that.getRentalId())
                && Objects.equals(getCustomerId(), that.getCustomerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRentalId(), getCustomerId());
    }

    @Override
    public String toString() {
        return getCustomerId() + "." + getRentalId() + "." + getId();
    }
}
