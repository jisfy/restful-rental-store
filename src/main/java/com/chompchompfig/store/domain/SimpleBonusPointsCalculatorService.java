package com.chompchompfig.store.domain;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A simple BonusPointsCalculator implementation with fixed points granted by Film category
 * @see BonusPointsCalculatorService
 */
@Service
public class SimpleBonusPointsCalculatorService implements BonusPointsCalculatorService {

    public static final int NEW_RELEASE_BONUS_POINTS = 2;
    public static final int OTHER_FILM_CATEGORY_BONUS_POINTS = 1;

    /**
     * @see BonusPointsCalculatorService#getBonusPointsForRental(Rental)
     */
    @Override
    public Long getBonusPointsForRental(Rental rental) {
        validateRental(rental);
        Optional<Long> rentalBonusPoints =
                rental.getItems().stream().map(i -> getBonusPoints(i.getFilm())).reduce(Long::sum);
        return rentalBonusPoints.orElse(0l);
    }

    /**
     * @see BonusPointsCalculatorService#getBonusPoints(Film)
     */
    @Override
    public Long getBonusPoints(Film film) {
        validateFilm(film);
        long bonusPoints;
        switch (film.getCategory()) {
            case NEW : bonusPoints = NEW_RELEASE_BONUS_POINTS; break;
            default : bonusPoints = OTHER_FILM_CATEGORY_BONUS_POINTS;
        }
        return bonusPoints;
    }

    /**
     * Performs a Film Null validation
     * @param film <p>the Film to validate</p>
     * @throws IllegalArgumentException <p>in case the Film is not valid</p>
     */
    void validateFilm(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Can't calculate bonus points for null film");
        }
    }

    /**
     * Performs a Rental Null validation
     * @param rental <p>the Rental to validate</p>
     * @throws IllegalArgumentException <p>in case the Rental is not valid</p>
     */
    void validateRental(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Can't calculate bonus points for null rental");
        }
    }
}
