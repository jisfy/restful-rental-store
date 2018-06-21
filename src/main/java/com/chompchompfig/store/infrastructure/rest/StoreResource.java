package com.chompchompfig.store.infrastructure.rest;

import io.swagger.annotations.ApiModel;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel(description = "A Resource representation of a Video Rental Store and all its operations", value = "Store")
public class StoreResource extends ResourceSupport {

    public static final String FILMS_REL_NAME = "films";
    public static final String CUSTOMERS_REL_NAME = "customers";
    public static final String RENTALS_REL_NAME = "rentals";
    public static final String PAYMENTS_REL_NAME = "payments";

    public StoreResource() {
        this.add(linkTo(methodOn(StoreController.class).getStore()).withSelfRel());
        this.add(linkTo(methodOn(FilmController.class).getFilms()).withRel(FILMS_REL_NAME));
        this.add(linkTo(methodOn(CustomerController.class).getCustomers()).withRel(CUSTOMERS_REL_NAME));
        this.add(linkTo(methodOn(RentalController.class).getRentals()).withRel(RENTALS_REL_NAME));
        this.add(linkTo(methodOn(PaymentController.class).getPayments()).withRel(PAYMENTS_REL_NAME));
    }
}
