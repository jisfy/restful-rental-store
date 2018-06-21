package com.chompchompfig.store.domain;

/**
 * A Service used to calculate the bonus points that should be rewarded to Customers for Rentals
 */
public interface BonusPointsCalculatorService {

    /**
     * Gets the bonus points that should be granted to a Customer for the given Rental. This takes into account
     * the bonus points granted for all Films in the Rental
     * @param rental <p>the Rental for which we would like to calculate the bonus points</p>
     * @return <p>the total bonus points to be granted to a Customer for a Rental</p>
     */
    Long getBonusPointsForRental(Rental rental);

    /**
     * Gets the bonus points that should be granted to a Customer for the given Film
     * @param film <p>the Film for which we would like to calculate the bonus points</p>
     * @return <p>the total bonus points to be granted to a Customer for a Film</p>
     */
    Long getBonusPoints(Film film);

}
