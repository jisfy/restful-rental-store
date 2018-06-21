package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Customer;
import com.chompchompfig.store.domain.SimpleFixtureFactory;
import com.chompchompfig.store.infrastructure.jpa.CustomerRepository;
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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerRepository customerRepository;
    private JsonPathTools jsonPathTools = new JsonPathTools();
    private SimpleFixtureFactory simpleFixtureFactory = new SimpleFixtureFactory();

    @Test
    public void storeCustomersShouldReturn200OkAndEmptyListBodyWhenNoCustomers() throws Exception {
        Mockito.when(customerRepository.findAll()).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/store/customers")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(content().json("{}"));
    }

    @Test
    public void storeCustomersShouldReturn200OkAndCorrectBodyWhenCustomersExist() throws Exception {
        List<Customer> customers = new ArrayList<>();
        customers.add(simpleFixtureFactory.newCustomerWithRentalWithNoItems(SimpleFixtureFactory.CUSTOMER_ID,
                SimpleFixtureFactory.CUSTOMER_FIRST_NAME, SimpleFixtureFactory.CUSTOMER_LAST_NAME,
                SimpleFixtureFactory.CUSTOMER_PHONE_NUMBER, SimpleFixtureFactory.RENTAL_ID,
                SimpleFixtureFactory.RENTAL_DAYS));
        customers.add(simpleFixtureFactory.newCustomerWithRentalWithNoItems(SimpleFixtureFactory.CUSTOMER_ID_2,
                SimpleFixtureFactory.CUSTOMER_FIRST_NAME_2, SimpleFixtureFactory.CUSTOMER_LAST_NAME_2,
                SimpleFixtureFactory.CUSTOMER_PHONE_NUMBER_2, SimpleFixtureFactory.RENTAL_ID_2,
                SimpleFixtureFactory.RENTAL_DAYS));

        Mockito.when(customerRepository.findAll()).thenReturn(customers);
        this.mockMvc.perform(get("/store/customers")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$._embedded['ex:customerResourceList'].length()").value(customers.size()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForCustomerPropertyInList(
                        0, "customerId")).value(customers.get(0).getId()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForCustomerPropertyInList(
                        0,"name")).value(customers.get(0).getFirstName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForCustomerPropertyInList(
                        0,"lastName")).value(customers.get(0).getLastName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForCustomerPropertyInList(
                        0,"_embedded.rentals.length()"))
                        .value(customers.get(0).getRentals().size()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForCustomerPropertyInList(
                        1,"customerId")).value(customers.get(1).getId()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForCustomerPropertyInList(
                        1,"name")).value(customers.get(1).getFirstName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForCustomerPropertyInList(
                        1,"lastName")).value(customers.get(1).getLastName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForCustomerPropertyInList(
                        1,"_embedded.rentals.length()"))
                        .value(customers.get(1).getRentals().size()));
    }

    @Test
    public void storeCustomerIdWithNonExistingIdShouldReturn404NotFound() throws Exception {
        Mockito.when(customerRepository.findById(SimpleFixtureFactory.CUSTOMER_ID)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/store/customer/" + SimpleFixtureFactory.CUSTOMER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void storeCustomerIdWithExistingIdShouldReturn200OkAndValidContent() throws Exception {
        Customer customer = simpleFixtureFactory.newCustomerWithRentalWithNoItems(SimpleFixtureFactory.CUSTOMER_ID,
                SimpleFixtureFactory.CUSTOMER_FIRST_NAME, SimpleFixtureFactory.CUSTOMER_LAST_NAME,
                SimpleFixtureFactory.CUSTOMER_PHONE_NUMBER, SimpleFixtureFactory.RENTAL_ID,
                SimpleFixtureFactory.RENTAL_DAYS);
        Mockito.when(customerRepository.findById(SimpleFixtureFactory.CUSTOMER_ID)).thenReturn(Optional.of(customer));
        this.mockMvc.perform(get("/store/customer/" + SimpleFixtureFactory.CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.customerId").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customer.getLastName()))
                .andExpect(jsonPath("$._embedded.rentals.length()").value(customer.getRentals().size()));
    }
}

