package com.chompchompfig.store.domain;

import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.Optional;

/**
 * A simple Stragegy implementation to calculate the price of a rental. Should hide the price calculation algorithm
 * which could change over time. Actual prices are hard-coded for now, but we could easily move them to a permanent
 * store if proved necessary -i.e. are subject to frequent changes-.
 */
@Service
public class CategoryBasedRentalPriceCalculatorService implements RentalPriceCalculatorService {

    public static final long PREMIUM_PRICE = 40;
    public static final long BASIC_PRICE = 30;
    public static final int REGULAR_FILMS_DAYS_AT_SINGLE_PRICE = 3;
    public static final int OLD_FILMS_DAYS_AT_SINGLE_PRICE = 5;

    private Currency priceCurrency = Currency.getInstance("SEK");

    /**
     * @see RentalPriceCalculatorService#getPriceCurrency()
     */
    @Override
    public Currency getPriceCurrency() {
        return priceCurrency;
    }

    /**
     * @see RentalPriceCalculatorService#getPrice(Rental)
     */
    @Override
    public long getPrice(Rental rental) {
        final Integer rentalDays = rental.getDays();
        return getPrice(rental, rentalDays);
    }

    /**
     * Gets the total price of renting all Films in a Rental for the given number of days
     * @param rental <p>the Rental for which we would like to get the total price</p>
     * @param rentalDays <p>the number of days of the Rental</p>
     * @return <p>the total price of renting all Films in a Rental for the given number of days</p>
     */
    long getPrice(Rental rental, int rentalDays) {
        Optional<Long> rentalPrice =
                rental.getItems().stream().map(ri -> getPrice(ri.getFilm(), rentalDays)).reduce((x, y) -> x + y);
        return rentalPrice.orElse(0L);
    }

    /**
     * Gets the total surcharge price of renting all Films in a Rental for the given number of days
     * @param rental <p>the Rental for which we would like to get the total price</p>
     * @param effectiveRentalDays <p>the effective number of days that the Rental actually lasted</p>
     * @return <p>the total surcharge price of renting all Films in a Rental for the given effective number of days</p>
     */
    long getSurchargePrice(Rental rental, long effectiveRentalDays) {
        final Integer rentalDays = rental.getDays();
        long rentalPrice = getPrice(rental, rentalDays);
        long effectiveRentalPrice = 0l;
        long surchargeRentalPrice = 0l;
        if (effectiveRentalDays > 0) {
            effectiveRentalPrice = getPrice(rental, (int) effectiveRentalDays);
            surchargeRentalPrice = effectiveRentalPrice - rentalPrice;
        }
        return surchargeRentalPrice;
    }

    /**
     * @see RentalPriceCalculatorService#getSurchargePrice(Rental)
     */
    @Override
    public long getSurchargePrice(Rental rental) {
        final long effectiveRentalDays = rental.getEffectiveDaysRented();
        return getSurchargePrice(rental, effectiveRentalDays);
    }

    /**
     * @see RentalPriceCalculatorService#getPrice(Film, int)
     */
    @Override
    public long getPrice(Film film, int daysRented) {
        validateDaysRented(daysRented);
        long rentalPrice;
        switch (film.getCategory()) {
            case NEW : rentalPrice = daysRented * PREMIUM_PRICE; break;
            case REGULAR : rentalPrice = getPriceWithSpecialRate(daysRented, REGULAR_FILMS_DAYS_AT_SINGLE_PRICE); break;
            case OLD : rentalPrice = getPriceWithSpecialRate(daysRented, OLD_FILMS_DAYS_AT_SINGLE_PRICE); break;
            default : throw new IllegalArgumentException("Can't calculate the price for unknown film category");
        }
        return rentalPrice;
    }

    /**
     * Gets the total price of a Rental of BASIC_PRICE, applying special rates
     * @param daysRented <p>the duration of the Rental in days</p>
     * @param daysAtSingleRate <p>the number of days of a Rental for which a special rate is charged</p>
     * @return <p>the total price of a Rental considering the special rates</p>
     */
    long getPriceWithSpecialRate(int daysRented, int daysAtSingleRate) {
        return BASIC_PRICE + (Math.max(daysRented - daysAtSingleRate, 0) * BASIC_PRICE);
    }

    /**
     * Performs the validation of the Rental duration. The number of days to rent can't be less or equal than zero
     * @param daysRented <p>the duration of the rental to validate</p>
     * @throws IllegalArgumentException <p>in case the number of days to rent is invalid</p>
     */
    void validateDaysRented(int daysRented) {
        if (daysRented <= 0) {
            throw new IllegalArgumentException("daysRented must be a non-zero positive integer");
        }
    }
}
