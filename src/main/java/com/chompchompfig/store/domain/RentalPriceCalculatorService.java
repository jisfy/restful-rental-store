package com.chompchompfig.store.domain;

import java.util.Currency;

/**
 * A Service to calculate the total price of Rentals
 */
public interface RentalPriceCalculatorService {

    /**
     * Gets the Currency used by all operations of this service
     * @return <p>the Currency in which all prices will be calculated by this service</p>
     */
    Currency getPriceCurrency();

    /**
     * Gets the total price of renting a Film for a given duration
     * @param film <p>the Film for which we would like to calculate the Rental price</p>
     * @param daysRented <p>the duration of the rental in days</p>
     * @return <p>the total price of renting a Film for a given duration</p>
     */
    long getPrice(Film film, int daysRented);

    /**
     * Gets the total price of renting a list of Films
     * @param rental <p>the Rental for which we would like to calculate the Rental price</p>
     * @return <p>the total price of renting all Films in a Rental</p>
     */
    long getPrice(Rental rental);

    /**
     * Gets the total surcharge price of renting a list of Films in a Rental. Surcharges are a consequence of not
     * returning the Films in a Rental on time
     * @param rental <p>the Rental for which we would like to calculate the Rental surcharge price</p>
     * @return <p>the total surcharge price of renting all Films in a Rental</p>
     */
    long getSurchargePrice(Rental rental);

}
