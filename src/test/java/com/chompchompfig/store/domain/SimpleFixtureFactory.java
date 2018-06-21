package com.chompchompfig.store.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SimpleFixtureFactory {

    public static final long CUSTOMER_ID = 0;
    public static final String CUSTOMER_FIRST_NAME = "John";
    public static final String CUSTOMER_LAST_NAME = "Florence";
    public static final String CUSTOMER_PHONE_NUMBER = "555-111-22-22";

    public static final long CUSTOMER_ID_2 = 1;
    public static final String CUSTOMER_FIRST_NAME_2 = "Michael";
    public static final String CUSTOMER_LAST_NAME_2 = "Jordan";
    public static final String CUSTOMER_PHONE_NUMBER_2 = "555-222-33-22";

    public static final long FILM_ID_1 = 221;
    public static final long FILM_ID_2 = 222;
    public static final long FILM_ID_3 = 223;

    public static final String FILM_NAME_1 = "Film Name 1";
    public static final String FILM_NAME_2 = "Film Name 2";
    public static final String FILM_NAME_3 = "Film Name 3";

    public static final long RENTAL_ID = 1234l;
    public static final long RENTAL_ID_2 = RENTAL_ID + 1;

    public static final int RENTAL_DAYS = 2;

    public static final long PAYMENT_ID_ID_PART_1 = 123243435;
    public static final long PAYMENT_ID_ID_PART_2 = 623243436;

    public static final PaymentId PAYMENT_ID_1 =
            PaymentId.from(CUSTOMER_ID + "." + RENTAL_ID + "." + PAYMENT_ID_ID_PART_1);
    public static final PaymentId PAYMENT_ID_2 =
            PaymentId.from(CUSTOMER_ID_2 + "." + RENTAL_ID_2 + "." + PAYMENT_ID_ID_PART_2);

    public static final String SEK_CURRENCY_CODE = "SEK";


    public Customer newCustomer() {
        return newCustomer(CUSTOMER_ID, CUSTOMER_FIRST_NAME, CUSTOMER_LAST_NAME, CUSTOMER_PHONE_NUMBER);
    }

    public Customer newCustomer(long id, String firstName, String lastName, String phoneNumber) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }

    public Rental newRentalWithItems() {
        List<Film> films = new ArrayList<>();
        films.add(newFilmOfCategory(0l, FILM_NAME_1, FilmCategory.NEW));
        films.add(newFilmOfCategory(1l, FILM_NAME_2, FilmCategory.OLD));
        films.add(newFilmOfCategory(2l, FILM_NAME_3, FilmCategory.REGULAR));
        return newRental(RENTAL_ID, RENTAL_DAYS, newCustomer(), films);
    }

    public Rental newRentalWithNoItems() {
        return newRental(RENTAL_ID, RENTAL_DAYS, newCustomer(), new ArrayList<>());
    }

    public Rental newSampleRental() {
        return newRental(RENTAL_ID, RENTAL_DAYS, newCustomer(), newFilms());
    }

    public Rental newRental(long id, int days, Customer customer, List<Film> films) {
        Rental rental = new Rental(customer, days, films);
        RentalId rentalId = RentalId.from(customer.getId() + "." + id);
        rental.setId(rentalId);
        return rental;
    }

    public List<RentalItem> newRentalItems(Rental rental) {
        RentalItemId rentalItemId1 = new RentalItemId(rental.getId().getCustomerId(), rental.getId().getId(), 0l);
        RentalItem rentalItem0 = new RentalItem();
        rentalItem0.setId(rentalItemId1);
        rentalItem0.setRental(rental);
        rentalItem0.setFilm(newFilmOfCategory(0l, FILM_NAME_1, FilmCategory.NEW));

        RentalItemId rentalItemId2 = new RentalItemId(rental.getId().getCustomerId(), rental.getId().getId(), 1l);
        RentalItem rentalItem1 = new RentalItem();
        rentalItem1.setId(rentalItemId2);
        rentalItem1.setRental(rental);
        rentalItem1.setFilm(newFilmOfCategory(1l, FILM_NAME_2, FilmCategory.OLD));

        RentalItemId rentalItemId3 = new RentalItemId(rental.getId().getCustomerId(), rental.getId().getId(), 2l);
        RentalItem rentalItem2 = new RentalItem();
        rentalItem2.setId(rentalItemId3);
        rentalItem2.setRental(rental);
        rentalItem2.setFilm(newFilmOfCategory(2l, FILM_NAME_3, FilmCategory.REGULAR));

        List<RentalItem> rentalItems = new ArrayList<>();
        rentalItems.add(rentalItem0);
        rentalItems.add(rentalItem1);
        rentalItems.add(rentalItem2);

        return rentalItems;
    }

    public List<Film> newFilms() {
        List<Film> films = new ArrayList<Film>();
        films.add(newFilmOfCategory(0l, FILM_NAME_1, FilmCategory.NEW));
        films.add(newFilmOfCategory(1l, FILM_NAME_2, FilmCategory.OLD));
        films.add(newFilmOfCategory(2l, FILM_NAME_3, FilmCategory.REGULAR));
        return films;
    }

    public Film newFilmOfCategory(Long id, String name, FilmCategory category) {
        Film film = new Film(name, category);
        film.setId(id);
        // film.setName(name);
        // film.setCategory(category);
        // film.setAvailable(true);
        return film;
    }

    public List<Customer> newCustomers() {
        List<Customer> customers = new ArrayList<>();
        customers.add(newCustomer(CUSTOMER_ID, CUSTOMER_FIRST_NAME, CUSTOMER_LAST_NAME, CUSTOMER_PHONE_NUMBER));
        customers.add(newCustomer(CUSTOMER_ID_2, CUSTOMER_FIRST_NAME_2, CUSTOMER_LAST_NAME_2, CUSTOMER_PHONE_NUMBER_2));
        return customers;
    }

    /**
     * Creates a Payment with no Rental. This should normally be invalid, but it is just a fixture for testing purposes
     * @param id <p>a PaymentId String, with format rentalId.id to use for the Payment. Even though there will be
     *           no associated Rental.</p>
     * @param amount <p>the Payment amount</p>
     * @param paymentStatus <p>the PaymentStatus to use for the Payment</p>
     * @return <p>a Payment instance with no Rental</p>
     */
    public Payment newPaymentShallow(String id, long amount, PaymentStatus paymentStatus) {
        Payment shallowPayment = new Payment();
        shallowPayment.setId(PaymentId.from(id));
        shallowPayment.setDate(new Date());
        shallowPayment.setStatus(paymentStatus);
        shallowPayment.setAmount(amount);
        shallowPayment.setCurrency(SEK_CURRENCY_CODE);
        return shallowPayment;
    }

    // TODO- long parameter list
    public Customer newCustomerWithRentalWithNoItems(long customerId, String customerFirstName,
            String customerLastName, String customerPhoneNumber, long rentalId, int rentalDays) {
        Customer customer =
                newCustomer(customerId, customerFirstName, customerLastName, customerPhoneNumber);
        Film dummyFilm = new Film(FILM_NAME_1, FilmCategory.OLD);
        customer.rent(rentalDays, Arrays.asList(dummyFilm));
        return customer;
    }

    // TODO- long parameter list
    public Customer newCustomerWithRentalWithItems(long customerId, String customerFirstName,
        String customerLastName, String customerPhoneNumber, long rentalId, int rentalDays, Film film) {
        Customer customer =
                newCustomer(customerId, customerFirstName, customerLastName, customerPhoneNumber);
        customer.rent(rentalDays, Arrays.asList(film));
        return customer;
    }

    Payment getPaymentForRentalWithPrice(Rental rental, long rentalPrice) {
        RentalPriceCalculatorService rentalPriceCalculatorService = new CategoryBasedRentalPriceCalculatorService();
        Payment payment = new Payment();
        PaymentId paymentId = new PaymentId(rental.getId().getCustomerId(), rental.getId().getId(), System.nanoTime());
        payment.setId(paymentId);
        payment.setRental(rental);
        payment.setAmount(rentalPrice);
        payment.setDate(new Date());
        payment.setCurrency(rentalPriceCalculatorService.getPriceCurrency().toString());
        return payment;
    }

}
