package com.chompchompfig.store.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@RunWith(SpringRunner.class)
public class RentalTests {

    public static final int RENTAL_START_YEAR = 2018;
    public static final int RENTAL_START_MONTH = 6;
    public static final int RENTAL_START_DAY = 1;

    public static final int RENTAL_RETURN_DATE_OVERDUE_YEAR = RENTAL_START_YEAR;
    public static final int RENTAL_RETURN_DATE_OVERDUE_MONTH = RENTAL_START_MONTH;
    public static final int RENTAL_RETURN_DATE_OVERDUE_DAY = 6;

    public static final int RENTAL_RETURN_DATE_NOT_OVERDUE_YEAR = RENTAL_START_YEAR;
    public static final int RENTAL_RETURN_DATE_NOT_OVERDUE_MONTH = RENTAL_START_MONTH;
    public static final int RENTAL_RETURN_DATE_NOT_OVERDUE_DAY = 2;

    public static final LocalDate RENTAL_START_DATE =
            LocalDate.of(RENTAL_START_YEAR, RENTAL_START_MONTH, RENTAL_START_DAY);
    public static final LocalDate RENTAL_RETURN_DATE_OVERDUE =
            LocalDate.of(RENTAL_RETURN_DATE_OVERDUE_YEAR, RENTAL_RETURN_DATE_OVERDUE_MONTH,
                    RENTAL_RETURN_DATE_OVERDUE_DAY);
    public static final LocalDate RENTAL_RETURN_DATE_NOT_OVERDUE =
            LocalDate.of(RENTAL_RETURN_DATE_NOT_OVERDUE_YEAR, RENTAL_RETURN_DATE_NOT_OVERDUE_MONTH,
                    RENTAL_RETURN_DATE_NOT_OVERDUE_DAY);


    private SimpleFixtureFactory simpleFixtureFactory = new SimpleFixtureFactory();
    private Rental rental;

    @Before
    public void setUpRental() {
        Date rentalStartDate = Date.from(RENTAL_START_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant());
        rental = simpleFixtureFactory.newRentalWithItems();
        rental.setRentalStartDate(rentalStartDate);
    }

    @Test
    public void getEffectiveDaysRentedShouldReturnCorrectNumberOfDays() {
        int expectedDaysRented = 5;
        long effectiveDaysRented = rental.getEffectiveDaysRented(RENTAL_RETURN_DATE_OVERDUE);
        Assert.assertEquals(expectedDaysRented, effectiveDaysRented);
    }

    @Test
    public void isOverdueShouldReturnTrueWhenEffectiveRentalDaysOver() {
        LocalDate currentDate = RENTAL_RETURN_DATE_OVERDUE;
        Assert.assertTrue(rental.isOverdue(currentDate));
    }

    @Test
    public void isOverdueShouldReturnFalseWhenEffectiveRentalDaysUnder() {
        LocalDate currentDate = RENTAL_RETURN_DATE_NOT_OVERDUE;
        Assert.assertFalse(rental.isOverdue(currentDate));
    }

}
