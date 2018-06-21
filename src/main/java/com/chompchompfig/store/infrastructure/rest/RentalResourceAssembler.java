package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Rental;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class RentalResourceAssembler extends ResourceAssemblerSupport<Rental, RentalResource> {

    public RentalResourceAssembler() {
        super(RentalController.class, RentalResource.class);
    }

    @Override
    public RentalResource toResource(Rental rental) {
        return new RentalResource(rental);
    }
}