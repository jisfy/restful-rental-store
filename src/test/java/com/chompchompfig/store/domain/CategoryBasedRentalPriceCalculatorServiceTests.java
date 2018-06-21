package com.chompchompfig.store.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class CategoryBasedRentalPriceCalculatorServiceTests {

    public static final String OUT_OF_AFRICA_OLD_FILM_NAME = "Out of Africa";
    public static final String SPIDER_MAN_REGULAR_FILM_NAME = "Spider Man";
    public static final String SPIDER_MAN_2_REGULAR_FILM_NAME = "Spider Man 2";
    public static final String MATRIX_11_NEW_FILM_NAME = "Matrix 11";

    private CategoryBasedRentalPriceCalculatorService rentalPriceCalculatorService;
    private SimpleFixtureFactory fixtureFactory;

    @Before
    public void setUpRentalService() {
        rentalPriceCalculatorService = new CategoryBasedRentalPriceCalculatorService();
        fixtureFactory = new SimpleFixtureFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateDaysRentedShouldThrowExceptionForNegativeDaysRented() {
        rentalPriceCalculatorService.validateDaysRented(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateDaysRentedShouldThrowExceptionForZeroDaysRented() {
        rentalPriceCalculatorService.validateDaysRented(0);
    }

    @Test
    public void validateDaysRentedShouldDoNothingForNonZeroPositiveDaysRented() {
        rentalPriceCalculatorService.validateDaysRented(1);
    }

    @Test
    public void getPriceWithSpecialRateShouldReturnCorrectPriceGivenUnderDaysAtSpecialRate() {
        int daysRented = CategoryBasedRentalPriceCalculatorService.REGULAR_FILMS_DAYS_AT_SINGLE_PRICE - 1;
        long rentalPrice = rentalPriceCalculatorService.getPriceWithSpecialRate(daysRented,
                CategoryBasedRentalPriceCalculatorService.REGULAR_FILMS_DAYS_AT_SINGLE_PRICE);
        assertEquals(CategoryBasedRentalPriceCalculatorService.BASIC_PRICE, rentalPrice);
    }

    @Test
    public void getPriceWithSpecialRateShouldReturnCorrectPriceGivenOverDaysAtSpecialRate() {
        int daysRented = CategoryBasedRentalPriceCalculatorService.REGULAR_FILMS_DAYS_AT_SINGLE_PRICE + 1;
        long rentalPrice = rentalPriceCalculatorService.getPriceWithSpecialRate(daysRented,
                CategoryBasedRentalPriceCalculatorService.REGULAR_FILMS_DAYS_AT_SINGLE_PRICE * 2);
        assertEquals(CategoryBasedRentalPriceCalculatorService.BASIC_PRICE, rentalPrice);
    }

    @Test
    public void getPriceShouldReturnCorrectPriceForNewFilms() {
        int daysRented = 1;
        long expectedRentalPrice = 40;
        Film matrix11NewFilm = fixtureFactory.newFilmOfCategory(0l, MATRIX_11_NEW_FILM_NAME, FilmCategory.NEW);
        long rentalPrice = rentalPriceCalculatorService.getPrice(matrix11NewFilm, daysRented);
        assertEquals(expectedRentalPrice , rentalPrice);
    }

    @Test
    public void getPriceShouldReturnCorrectPriceForRegularFilmsUnderDaysAtSpecialRate() {
        long expectedRentalPrice = 30;
        getPriceShouldReturnExpectedPriceForFilmCategory(2,expectedRentalPrice,
                SPIDER_MAN_2_REGULAR_FILM_NAME, FilmCategory.REGULAR);
    }

    @Test
    public void getPriceShouldReturnCorrectPriceForRegularFilmsOverDaysAtSpecialRate() {
        long expectedRentalPrice = 90;
        getPriceShouldReturnExpectedPriceForFilmCategory(5, expectedRentalPrice,
                SPIDER_MAN_REGULAR_FILM_NAME, FilmCategory.REGULAR);
    }

    @Test
    public void getPriceShouldReturnCorrectPriceForOldFilmsUnderDaysAtSpecialRate() {
        long expectedRentalPrice = CategoryBasedRentalPriceCalculatorService.BASIC_PRICE;
        getPriceShouldReturnExpectedPriceForFilmCategory(5, expectedRentalPrice,
                OUT_OF_AFRICA_OLD_FILM_NAME, FilmCategory.OLD);
    }

    @Test
    public void getPriceShouldReturnCorrectPriceForOldFilmsOverDaysAtSpecialRate() {
        long expectedRentalPrice = 90;
        getPriceShouldReturnExpectedPriceForFilmCategory(7,expectedRentalPrice,
                OUT_OF_AFRICA_OLD_FILM_NAME, FilmCategory.OLD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPriceShouldThrowAnExceptionGivenInvalidDaysRented() {
        int invalidDaysRented = -1;
        Film matrix11NewFilm = fixtureFactory.newFilmOfCategory(0l, MATRIX_11_NEW_FILM_NAME,  FilmCategory.NEW);
        rentalPriceCalculatorService.getPrice(matrix11NewFilm, invalidDaysRented);
    }

    @Test
    public void getPriceRentalShouldReturnCorrectPriceForMultipleRentalItems() {
        Rental rental = fixtureFactory.newRentalWithItems();
        long expectedRentalPrice = (CategoryBasedRentalPriceCalculatorService.PREMIUM_PRICE * rental.getDays()) +
                (CategoryBasedRentalPriceCalculatorService.BASIC_PRICE * 2);
        long rentalPrice = rentalPriceCalculatorService.getPrice(rental, rental.getDays());
        assertEquals(expectedRentalPrice, rentalPrice);
    }

    @Test
    public void getPriceRentalShouldReturnZeroForEmptyRentalItems() {
        Rental rental = fixtureFactory.newRentalWithNoItems();
        long expectedRentalPrice = 0L;
        long rentalPrice = rentalPriceCalculatorService.getPrice(rental, rental.getDays());
        assertEquals(expectedRentalPrice, rentalPrice);
    }

    @Test
    public void getSurchargePriceShouldReturnCorrectValueForMultipleRentalItemsAndTwoDaysOverdue() {
        int effectiveRentalDays = fixtureFactory.RENTAL_DAYS + 2;
        long expectedSurchargePrice =
                (CategoryBasedRentalPriceCalculatorService.PREMIUM_PRICE * fixtureFactory.RENTAL_DAYS) +
                        (CategoryBasedRentalPriceCalculatorService.BASIC_PRICE);
        getSurchargePriceShouldReturnExpectedValueForMultipleRentalItemsAndEffectiveDays(effectiveRentalDays,
                expectedSurchargePrice);
    }

    @Test
    public void getSurchargePriceShouldReturnZeroForMultipleRentalItemsAndZeroDaysOverdue() {
        int effectiveRentalDays = fixtureFactory.RENTAL_DAYS;
        long expectedSurchargePrice = 0l;
        getSurchargePriceShouldReturnExpectedValueForMultipleRentalItemsAndEffectiveDays(effectiveRentalDays,
                expectedSurchargePrice);
    }

    public void getSurchargePriceShouldReturnExpectedValueForMultipleRentalItemsAndEffectiveDays(
            int effectiveRentalDays, long expectedSurchargePrice) {
        Rental rental = fixtureFactory.newRentalWithItems();
        long surchargePrice = rentalPriceCalculatorService.getSurchargePrice(rental, effectiveRentalDays);
        assertEquals(expectedSurchargePrice, surchargePrice);
    }

    private void getPriceShouldReturnExpectedPriceForFilmCategory(int days, long expectedPrice,
                                                                  String name, FilmCategory category) {
        Film someNewFilm = fixtureFactory.newFilmOfCategory(0l, name, category);
        long rentalPrice = rentalPriceCalculatorService.getPrice(someNewFilm, days);
        assertEquals(expectedPrice, rentalPrice);
    }
}
