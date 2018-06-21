package com.chompchompfig.store.domain;

import com.chompchompfig.store.infrastructure.jpa.CustomerRepository;
import com.chompchompfig.store.infrastructure.jpa.FilmRepository;
import com.chompchompfig.store.infrastructure.jpa.PaymentRepository;
import com.chompchompfig.store.infrastructure.jpa.RentalRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ComponentScan({"com.chompchompfig.store.domain", "com.chompchompfig.store.application"})
@Configuration
public class TestContextConfiguration {

    @Primary
    @Bean
    public PaymentRepository paymentRepository() {
        return Mockito.mock(PaymentRepository.class);
    }

    @Primary
    @Bean
    public FilmRepository filmRepository() {
        return Mockito.mock(FilmRepository.class);
    }

    @Primary
    @Bean
    public RentalRepository rentalRepository() {
        return Mockito.mock(RentalRepository.class);
    }

    @Primary
    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }
}
