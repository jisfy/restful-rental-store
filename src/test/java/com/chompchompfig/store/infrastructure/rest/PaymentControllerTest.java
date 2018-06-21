package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Payment;
import com.chompchompfig.store.domain.PaymentStatus;
import com.chompchompfig.store.domain.SimpleFixtureFactory;
import com.chompchompfig.store.infrastructure.jpa.PaymentRepository;
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
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PaymentRepository paymentRepository;
    private JsonPathTools jsonPathTools = new JsonPathTools();
    private SimpleFixtureFactory simpleFixtureFactory = new SimpleFixtureFactory();

    @Test
    public void storePaymentsShouldReturn200OkAndEmptyListBodyWhenNoFilms() throws Exception {
        Mockito.when(paymentRepository.findAll()).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/store/payments")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(content().json("{}"));
    }

    @Test
    public void storePaymentsShouldReturn200OkAndCorrectBodyWhenPaymentsExist() throws Exception {
        Payment payment1 =
                simpleFixtureFactory.newPaymentShallow(SimpleFixtureFactory.PAYMENT_ID_1.toString(),
                        100, PaymentStatus.DONE);
        Payment payment2 =
                simpleFixtureFactory.newPaymentShallow(SimpleFixtureFactory.PAYMENT_ID_2.toString(),
                        102, PaymentStatus.PENDING);
        List<Payment> payments = new ArrayList<>();
        payments.add(payment1);
        payments.add(payment2);

        Mockito.when(paymentRepository.findAll()).thenReturn(payments);
        this.mockMvc.perform(get("/store/payments")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$._embedded['ex:paymentResourceList'].length()").value(payments.size()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(0, "paymentId.rentalId"))
                        .value(payments.get(0).getId().getRentalId()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(0, "paymentId.id"))
                        .value(payments.get(0).getId().getId()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(0, "amount"))
                        .value(payments.get(0).getAmount()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(0, "currency"))
                        .value(payments.get(0).getCurrency()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(0, "status"))
                        .value(payments.get(0).getStatus().toString()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(1, "paymentId.rentalId"))
                        .value(payments.get(1).getId().getRentalId()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(1, "paymentId.id"))
                        .value(payments.get(1).getId().getId()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(1, "amount"))
                        .value(payments.get(1).getAmount()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(1, "currency"))
                        .value(payments.get(1).getCurrency()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForPaymentPropertyInList(1, "status"))
                        .value(payments.get(1).getStatus().toString()));

        // code duplication!!!. generalize expectations
    }

    @Test
    public void storePaymentIdWithNonExistingIdShouldReturn404NotFound() throws Exception {
        Mockito.when(paymentRepository.findById(SimpleFixtureFactory.PAYMENT_ID_1)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/store/payment/" + SimpleFixtureFactory.PAYMENT_ID_1.toString())).
                andExpect(status().isNotFound());
    }

    @Test
    public void storePaymentIdWithExistingIdShouldReturn200OkAndValidContent() throws Exception {
        Payment somePayment =
                simpleFixtureFactory.newPaymentShallow(SimpleFixtureFactory.PAYMENT_ID_1.toString(),
                        100, PaymentStatus.PENDING);
        Mockito.when(paymentRepository.findById(SimpleFixtureFactory.PAYMENT_ID_1)).
                thenReturn(Optional.of(somePayment));
        this.mockMvc.perform(get("/store/payment/" + SimpleFixtureFactory.PAYMENT_ID_1.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId.rentalId").value(somePayment.getId().getRentalId()))
                .andExpect(jsonPath("$.paymentId.id").value(somePayment.getId().getId()))
                .andExpect(jsonPath("$.amount").value(somePayment.getAmount()))
                .andExpect(jsonPath("$.currency").value(somePayment.getCurrency()))
                .andExpect(jsonPath("$.status").value(somePayment.getStatus().toString()));
        // extract method with expectations
    }
}

