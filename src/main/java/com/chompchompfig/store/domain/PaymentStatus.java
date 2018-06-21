package com.chompchompfig.store.domain;

/**
 * Reflects the different statuses of a Payment. PENDING indicates that the Payment hasn't been completed yet. This is
 * the Customer has not delivered the money to the business owner. DONE reflects a Payment that was finally performed
 * by the Customer. This is, the customer delivered the money to the business owner.
 */
public enum PaymentStatus {
    PENDING, DONE
}
