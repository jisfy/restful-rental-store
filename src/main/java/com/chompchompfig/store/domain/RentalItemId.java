package com.chompchompfig.store.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Value Object representing a RentalItem Identifier
 */
@Embeddable
public class RentalItemId implements Serializable {

    public static final String RENTAL_ITEM_ID_PARSING_ERROR_MESSAGE = "" +
            "The supplied RentalId must be three long numbers separated by a dot . like so [customerId.rentalId.id]";

    @Column(name="CUSTOMER_ID")
    private Long customerId;
    @Column(name="RENTAL_ID")
    private Long rentalId;
    @Column(name="ID")
    private Long id;

    protected RentalItemId() {
    }

    public RentalItemId(Long customerId, Long rentalId, Long id) {
        this.customerId = customerId;
        this.rentalId = rentalId;
        this.id = id;
    }

    /**
     * Gets the RentalItemId CustomerId part
     * @return <p>the customerId part of a RentalItemId</p>
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * Gets the RentalId part of a RentalItemId
     * @return <p>the RentalId part of a RentalItemId</p>
     */
    public Long getRentalId() {
        return rentalId;
    }

    /**
     * Gets the Id part of a RentalItemId
     * @return <p>the Id part of a RentalItemId</p>
     */
    public Long getId() {
        return id;
    }

    /**
     * Creates a RentalItem Identifier from the given Rental Id String. Typically a String with the format
     * CustomerId.RentalId.Timestamp
     * @param rentalItemIdString <p>the RentalItem Id string with the format described above</p>
     * @return <p>a RentalItemId instance whose parts where parsed from the given input string</p>
     * @throws IllegalArgumentException <p>in case the input String can't be parsed successfully</p>
     */
    public static RentalItemId from(String rentalItemIdString) {
        if (rentalItemIdString == null) {
            throw new IllegalArgumentException(RENTAL_ITEM_ID_PARSING_ERROR_MESSAGE);
        }

        String rentalItemIdParts[] = rentalItemIdString.split("\\.");
        if (rentalItemIdParts.length != 3) {
            throw new IllegalArgumentException(RENTAL_ITEM_ID_PARSING_ERROR_MESSAGE);
        }

        try {
            String rentalItemCustomerIdPart = rentalItemIdParts[0];
            String rentalItemRentalIdPart = rentalItemIdParts[1];
            String idPart = rentalItemIdParts[2];

            Long rentalItemCustomerId = Long.parseLong(rentalItemCustomerIdPart);
            Long rentalItemRentalId = Long.parseLong(rentalItemRentalIdPart);
            Long id = Long.parseLong(idPart);
            return new RentalItemId(rentalItemCustomerId, rentalItemRentalId, id);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(RENTAL_ITEM_ID_PARSING_ERROR_MESSAGE);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RentalItemId)) return false;
        RentalItemId that = (RentalItemId) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCustomerId(), that.getCustomerId())
                && Objects.equals(getRentalId(), that.getRentalId());
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

