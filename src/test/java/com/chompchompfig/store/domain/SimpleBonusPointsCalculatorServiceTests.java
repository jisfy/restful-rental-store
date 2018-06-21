package com.chompchompfig.store.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public class SimpleBonusPointsCalculatorServiceTests {

    @Autowired
    private SimpleBonusPointsCalculatorService bonusPointsCalculatorService;
    private SimpleFixtureFactory fixtureFactory = new SimpleFixtureFactory();

    @Test
    public void getBonusPointsForNewFilmShouldReturnCorrectBonusPoints() {
        getBonusPointsForFilmOfCategoryShouldReturnCorrectBonusPoints(FilmCategory.NEW,
                SimpleBonusPointsCalculatorService.NEW_RELEASE_BONUS_POINTS);
    }

    @Test
    public void getBonusPointsForOldFilmShouldReturnCorrectBonusPoints() {
        getBonusPointsForFilmOfCategoryShouldReturnCorrectBonusPoints(FilmCategory.OLD,
                SimpleBonusPointsCalculatorService.OTHER_FILM_CATEGORY_BONUS_POINTS);
    }

    @Test
    public void getBonusPointsForRegularFilmShouldReturnCorrectBonusPoints() {
        getBonusPointsForFilmOfCategoryShouldReturnCorrectBonusPoints(FilmCategory.REGULAR,
                SimpleBonusPointsCalculatorService.OTHER_FILM_CATEGORY_BONUS_POINTS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBonusPointsForNullFilmShouldThrowException() {
        bonusPointsCalculatorService.getBonusPoints(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBonusPointsForRentalForNullRentalShouldThrowException() {
        bonusPointsCalculatorService.getBonusPointsForRental(null);
    }

    @Test
    public void getBonusPointsForRentalForEmptyRentalShouldReturnZero() {
        Rental rentalWithNoItems = fixtureFactory.newRentalWithNoItems();
        Assert.assertNotNull(rentalWithNoItems);
        Assert.assertEquals(0, rentalWithNoItems.getItems().size());
        long bonusPointsForRental = bonusPointsCalculatorService.getBonusPointsForRental(rentalWithNoItems);
        Assert.assertEquals(0, bonusPointsForRental);
    }

    @Test
    public void getBonusPointsForRentalForValidRentalShouldReturnCorrectValue() {
        Rental rentalWithItems = fixtureFactory.newRentalWithItems();
        Assert.assertNotNull(rentalWithItems);
        Assert.assertEquals(3, rentalWithItems.getItems().size());
        long expectedBonusPoints = 4;
        long bonusPointsForRental = bonusPointsCalculatorService.getBonusPointsForRental(rentalWithItems);
        Assert.assertEquals(expectedBonusPoints, bonusPointsForRental);
    }

    private void getBonusPointsForFilmOfCategoryShouldReturnCorrectBonusPoints(FilmCategory filmCategory,
                                                                              long expectedBonusPoints) {
        Film newFilm = fixtureFactory.newFilmOfCategory(0l, SimpleFixtureFactory.FILM_NAME_1, filmCategory);
        long bonusPointsOfNewFilm = bonusPointsCalculatorService.getBonusPoints(newFilm);
        Assert.assertEquals(expectedBonusPoints, bonusPointsOfNewFilm);
    }

}
