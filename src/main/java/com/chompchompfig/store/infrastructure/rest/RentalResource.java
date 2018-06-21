package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.*;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel(description = "A representation of a Rental resource", value = "Rental")
@JsonPropertyOrder({"rentalId", "status", "customer", "rentalStartDate", "days", "overdue", "films", "_embedded", "_links"})
public class RentalResource extends ResourceSupport {

    public static final String PAY_REL_NAME = "pay";
    public static final String MODIFY_REL_NAME = "modify";
    public static final String RETURN_REL_NAME = "return";
    public static final String FILMS_EMBEDDED_REL_NAME = "films";
    public static final String CUSTOMER_EMBEDDED_REL_NAME = "customer";
    public static final String RENTAL_RESOURCE_PATH = "rental/";

    private Rental rental;

    public RentalResource(Rental rental) {
        this.rental = rental;
        this.add(linkTo(methodOn(RentalController.class).getRental(this.rental.getId().toString())).withSelfRel());
        addModifyLinks();
        addReturnLinks();
        addPaymentLinks();
    }

    private void addModifyLinks() {
        if (rental.getStatus().equals(RentalStatus.AWAITING_PAYMENT)) {
            this.add(linkTo(RentalController.class)
                    .slash(RENTAL_RESOURCE_PATH + rental.getId().toString()).withRel(MODIFY_REL_NAME));
            //  I hate this. There doesn't seem a way to reference a Controller method with a ControllerLinkBuilder
        }
    }

    private void addReturnLinks() {
        if (rental.getStatus().equals(RentalStatus.PAID)) {
            this.add(linkTo(methodOn(RentalController.class)
                    .returnRental(rental.getId().toString())).withRel(RETURN_REL_NAME));
        }
    }

    private void addPaymentLinks() {
        if (rental.getStatus().equals(RentalStatus.AWAITING_PAYMENT_OVERDUE) ||
                rental.getStatus().equals(RentalStatus.AWAITING_PAYMENT)) {
            List<Payment> pendingPayments =
                    rental.getPayments().stream().filter(p -> p.getStatus().equals(PaymentStatus.PENDING)).
                            collect(Collectors.toList());
            pendingPayments.stream().forEach(p -> addPaymentLink(p));
        }
    }

    private void addPaymentLink(Payment payment) {
        this.add(linkTo(methodOn(PaymentController.class).fulfillPayment(payment.getId().toString())).
                withRel(PAY_REL_NAME));
    }

    @ApiModelProperty(notes = "the Rental unique identifier")
    public RentalId getRentalId() {
        return this.rental.getId();
    }

    @ApiModelProperty(notes = "the Rental Status. Can be AWAITING_PAYMENT, PAID, AWAITING_PAYMENT_OVERDUE, RETURNED " +
            "OR CANCELLED")
    public RentalStatus getStatus() {
        return this.rental.getStatus();
    }

    @ApiModelProperty(notes = "the start date of the Rental")
    public Date getRentalStartDate() {
        return this.rental.getRentalStartDate();
    }

    @ApiModelProperty(notes = "the duration of the Rental in days")
    public Integer getDays() {
        return this.rental.getDays();
    }

    @ApiModelProperty(notes = "is the Rental past its predefined duration?")
    public Boolean isOverdue() {
        return this.rental.isOverdue();
    }

    @ApiModel(description = "A brief representation of a Customer resource", value = "Customer")
    @JsonPropertyOrder({"customerId", "name", "_links"})
    public static class LightWeightCustomerResourceRepresentation extends ResourceSupport {

        private Customer customer;

        public LightWeightCustomerResourceRepresentation(Customer customer) {
            this.customer = customer;
            this.add(linkTo(methodOn(CustomerController.class).getCustomer(this.customer.getId())).withSelfRel());
        }

        @ApiModelProperty(notes = "the Customer unique identifier")
        public Long getCustomerId() {
            return this.customer.getId();
        }

        @ApiModelProperty(notes = "the Customer name")
        public String getName() {
            return this.customer.getLastName() + ", " + this.customer.getFirstName();
        }

    }
    @ApiModel(description = "A brief representation of a Film resource", value = "Film")
    @JsonPropertyOrder({"filmId", "name", "_links"})
    public static class LightWeightFilmResourceRepresentation extends ResourceSupport {

        private Film film;

        public LightWeightFilmResourceRepresentation(Film film) {
            this.film = film;
            this.add(linkTo(methodOn(FilmController.class).getFilm(this.film.getId())).withSelfRel());
        }

        @ApiModelProperty(notes = "the Film unique identifier")
        public Long getFilmId() {
            return this.film.getId();
        }

        @ApiModelProperty(notes = "the Film name")
        public String getName() {
            return this.film.getName();
        }

    }

    @JsonUnwrapped
    public Resources<EmbeddedWrapper> getEmbedded() {
        EmbeddedWrappers wrappers = new EmbeddedWrappers(true);
        List<EmbeddedWrapper> embeddedWrappers = new ArrayList<>();
        LightWeightCustomerResourceRepresentation lightWeightCustomerResourceRepresentation =
                new LightWeightCustomerResourceRepresentation(this.rental.getCustomer());
        embeddedWrappers.add(wrappers.wrap(lightWeightCustomerResourceRepresentation, CUSTOMER_EMBEDDED_REL_NAME));

        List<EmbeddedWrapper> lightWeightFilms = this.rental.getItems()
                .stream().map(ri -> wrappers.wrap(new LightWeightFilmResourceRepresentation(ri.getFilm()),
                        FILMS_EMBEDDED_REL_NAME)).collect(Collectors.toList());
        embeddedWrappers.addAll(lightWeightFilms);

        return new Resources(embeddedWrappers);
    }
}
