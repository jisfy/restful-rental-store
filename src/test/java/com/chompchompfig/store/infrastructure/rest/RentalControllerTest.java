package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.application.VideoRentalService;
import com.chompchompfig.store.domain.*;
import com.chompchompfig.store.infrastructure.jpa.RentalRepository;
import com.chompchompfig.store.tools.JsonPathTools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RentalController.class)
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RentalRepository rentalRepository;
    @MockBean
    private VideoRentalService videoRentalService;
    private JsonPathTools jsonPathTools = new JsonPathTools();
    private SimpleFixtureFactory simpleFixtureFactory = new SimpleFixtureFactory();

    @Test
    public void storeRentalsShouldReturn200OkAndEmptyListBodyWhenNoRentals() throws Exception {
        Mockito.when(rentalRepository.findAll()).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/store/rentals")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(content().json("{}"));
    }

    @Test
    public void storeRentalsShouldReturn200OkAndCorrectBodyWhenRentalsExist() throws Exception {
        // The payment needs to be added too
        Film film1 = simpleFixtureFactory.newFilmOfCategory(
                SimpleFixtureFactory.FILM_ID_1, SimpleFixtureFactory.FILM_NAME_1, FilmCategory.OLD);
        Film film2 = simpleFixtureFactory.newFilmOfCategory(
                SimpleFixtureFactory.FILM_ID_2, SimpleFixtureFactory.FILM_NAME_2, FilmCategory.OLD);

        Customer customer1 = simpleFixtureFactory.newCustomerWithRentalWithItems(SimpleFixtureFactory.CUSTOMER_ID,
                SimpleFixtureFactory.CUSTOMER_FIRST_NAME, SimpleFixtureFactory.CUSTOMER_LAST_NAME,
                SimpleFixtureFactory.CUSTOMER_PHONE_NUMBER, SimpleFixtureFactory.RENTAL_ID,
                SimpleFixtureFactory.RENTAL_DAYS, film1);
        Customer customer2 = simpleFixtureFactory.newCustomerWithRentalWithItems(SimpleFixtureFactory.CUSTOMER_ID_2,
                SimpleFixtureFactory.CUSTOMER_FIRST_NAME_2, SimpleFixtureFactory.CUSTOMER_LAST_NAME_2,
                SimpleFixtureFactory.CUSTOMER_PHONE_NUMBER_2, SimpleFixtureFactory.RENTAL_ID_2,
                SimpleFixtureFactory.RENTAL_DAYS, film2);

        List<Rental> rentals = new ArrayList<>();
        rentals.addAll(customer1.getRentals());
        rentals.addAll(customer2.getRentals());

        Mockito.when(rentalRepository.findAll()).thenReturn(rentals);
        this.mockMvc.perform(get("/store/rentals")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$._embedded['ex:rentals'].length()").value(rentals.size()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(0, "days"))
                        .value(SimpleFixtureFactory.RENTAL_DAYS))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(0, "status"))
                        .value(RentalStatus.AWAITING_PAYMENT.toString()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(0, "_embedded.films.length()"))
                        .value(customer1.getRentals().get(0).getItems().size()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(0, "_embedded.films[0].name"))
                        .value(film1.getName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(0, "_embedded.customer[0].name"))
                        .value(customer1.getLastName() + ", " + customer1.getFirstName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(1, "days"))
                        .value(SimpleFixtureFactory.RENTAL_DAYS))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(1, "status"))
                        .value(RentalStatus.AWAITING_PAYMENT.toString()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(1, "_embedded.films.length()"))
                        .value(customer2.getRentals().get(0).getItems().size()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(1, "_embedded.films[0].name"))
                        .value(film2.getName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForRentalPropertyInList(1, "_embedded.customer[0].name"))
                        .value(customer2.getLastName() + ", " + customer2.getFirstName()));
    }

}
