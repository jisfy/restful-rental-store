package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Customer;
import com.chompchompfig.store.domain.Rental;
import com.chompchompfig.store.domain.RentalId;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel(description = "A representation of a Customer resource", value = "Customer")
@JsonPropertyOrder({"customerId", "name", "lastName", "pointsInCard", "_embedded", "_links"})
public class CustomerResource extends ResourceSupport {

    private Customer customer;

    public CustomerResource(Customer customer) {
        this.customer = customer;
        this.add(linkTo(methodOn(CustomerController.class).getCustomer(this.customer.getId())).withSelfRel());
    }

    @ApiModelProperty(notes = "the customer's unique identifier")
    public Long getCustomerId() {
        return this.customer.getId();
    }

    @ApiModelProperty(notes = "the customer's firstname")
    public String getName() {
        return this.customer.getFirstName();
    }

    @ApiModelProperty(notes = "the customer's lastname")
    public String getLastName() {
        return this.customer.getLastName();
    }

    @ApiModelProperty(notes = "the number of bonus points in the customer's card")
    public Long getPointsInCard() {
        return this.customer.getPointsInCard();
    }

    @ApiModelProperty(notes = "all the customer's Film rentals")
    @JsonUnwrapped
    public Resources<EmbeddedWrapper> getRentals() {
        EmbeddedWrappers wrappers = new EmbeddedWrappers(true);
        List<EmbeddedWrapper> rentals = this.customer.getRentals()
                .stream().map(r -> wrappers.wrap(new LightWeightRentalResourceRepresentation(r), "rentals"))
                        .collect(Collectors.toList());
        return new Resources(rentals);
    }

    @ApiModel(description = "A brief representation of a Film rental", value = "Rental")
    @JsonPropertyOrder({"rentalId", "_links"})
    public static class LightWeightRentalResourceRepresentation extends ResourceSupport {

        private Rental rental;

        public LightWeightRentalResourceRepresentation(Rental rental) {
            this.rental = rental;
            this.add(linkTo(methodOn(RentalController.class).getRental(this.rental.getId().toString())).withSelfRel());
        }

        public RentalId getRentalId() {
            return this.rental.getId();
        }

    }

}
