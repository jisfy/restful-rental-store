package com.chompchompfig.store.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Value Object to uniquely identify Rentals. This is, a Rental identifier
 */
@Embeddable
public class RentalId implements Serializable {

    public static final String RENTAL_ID_PARSING_ERROR_MESSAGE = "" +
            "The supplied RentalId must be two long numbers separated by a dot . like so [customerId.id]";

    @Column(name="CUSTOMER_ID")
    private Long customerId;
    @Column(name="ID")
    private Long id;

    protected RentalId() {
    }

    public RentalId(Long customerId, Long id) {
        this.customerId = customerId;
        this.id = id;
    }

    /**
     * Gets the RentalId CustomerId part
     * @return <p>the customerId part of a RentalId</p>
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * Gets the Id part of a RentalId
     * @return <p>the Id part of a RentalId</p>
     */
    public Long getId() {
        return id;
    }

    /**
     * Creates a Payment Identifier from the given Payment Id String. Typically a String with the format
     * RentalId.Timestamp
     * @param rentalIdString <p>the Rental Id string with the format described above</p>
     * @return <p>a CustomerId instance whose parts where parsed from the given input string</p>
     * @throws IllegalArgumentException <p>in case the input String can't be parsed successfully</p>
     */
    public static RentalId from(String rentalIdString) {
        if (rentalIdString == null) {
            throw new IllegalArgumentException(RENTAL_ID_PARSING_ERROR_MESSAGE);
        }

        String rentalIdParts[] = rentalIdString.split("\\.");
        if (rentalIdParts.length != 2) {
            throw new IllegalArgumentException(RENTAL_ID_PARSING_ERROR_MESSAGE);
        }

        try {
            String rentalIdPart = rentalIdParts[0];
            String idPart = rentalIdParts[1];
            Long rentalId = Long.parseLong(rentalIdPart);
            Long id = Long.parseLong(idPart);
            return new RentalId(rentalId, id);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(RENTAL_ID_PARSING_ERROR_MESSAGE);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RentalId)) return false;
        RentalId that = (RentalId) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getCustomerId(), that.getCustomerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCustomerId());
    }

    @Override
    public String toString() {
        return getCustomerId() + "." + getId();
    }
}
