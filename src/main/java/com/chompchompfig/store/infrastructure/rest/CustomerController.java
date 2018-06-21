package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.infrastructure.jpa.CustomerRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Api(value ="Video Rentals, Customers", description = "Customer API", tags = "{2}")
@RestController
@RequestMapping("/store")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerResourceAssembler resourceAssembler;

    @ApiOperation(value = "Get the list of registered Customers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved")
    })
    @GetMapping(value = "/customers", produces = MediaTypes.HAL_JSON_VALUE)
    public Resources<CustomerResource> getCustomers() {
        return new Resources(resourceAssembler.toResources(customerRepository.findAll()));
    }

    @ApiOperation(value = "Gets a registered Customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved"),
            @ApiResponse(code = 404, message = "The selected Customer does not exist")
    })
    @GetMapping(value = "/customer/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public CustomerResource getCustomer(@PathVariable Long id) {
        Optional<CustomerResource> customerResource =
                customerRepository.findById(id).map(c -> resourceAssembler.toResource(c));
        return customerResource.get();
    }
}
