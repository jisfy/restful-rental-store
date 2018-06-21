package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Customer;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CustomerResourceAssembler extends ResourceAssemblerSupport<Customer, CustomerResource> {

    public CustomerResourceAssembler() {
        super(CustomerController.class, CustomerResource.class);
    }

    @Override
    public CustomerResource toResource(Customer customer) {
        return new CustomerResource(customer);
    }
}
