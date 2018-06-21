package com.chompchompfig.store.domain;

/**
 * The RentalStatus represents the different statuses in which a Rental could be.
 * <ul>
 *     <li>AWAITING_PAYMENT is the initial status of a Rental. When a Rental is in this status, the Customer still
 *     needs to perform for it. The films are still available -on the shelf-, so they can be picked by a different
 *     Customer. No bonus points have been rewarded to the Customer</li>
 *     <li>PAID is the status in which a Rental would be when paid by the Customer. A Rental in this status would have
 *     an associated Payment that will have been performed by the Customer, and whose amount will match of the total
 *     price of the Rental. The films associated with this status will all be unavailable for others at this point.
 *     The Customer will have been rewarded with the corresponding bonus points</li>
 *     <li>CANCELLED, a Rental is canceled when the Customer no longer wants to proceed and perform for it. No charges will
 *     be associated with this Rental, and it will be closed for further changes. The films associated with this
 *     Rental will all remain available. A Rental can only transition into the CANCELLED state from AWAITING_PAYMENT</li>
 *     <li>AWAITING_PAYMENT_OVERDUE. A Rental is in this state when it is coming from a former PAID state, but its
 *     duration has been exceeded. A Rental of this kind will incur in additional charges, which will need to be
 *     satisfied by the Customer in order to move on to the RETURNED state. While the Rental is in this state, the
 *     corresponding associated Films will remain unavailable to others. The only other state a Rental can transition to
 *     from this state is RETURNED</li>
 *     <li>RETURNED is the final state of a Rental Finite State Machine. The Rental can end up in this state either
 *     coming from PAID, when the duration has not been exceeded, or also from the AWAITING_PAYMENT_OVERDUE, when the
 *     extra charges have been satisfied by the Customer. Upon reaching this state, the Rental will have all its
 *     Films released and available to other Customers. There is no other transition posible from this state</li>
 * </ul>
 */
public enum RentalStatus {
    AWAITING_PAYMENT, PAID, CANCELLED, AWAITING_PAYMENT_OVERDUE, RETURNED
}
