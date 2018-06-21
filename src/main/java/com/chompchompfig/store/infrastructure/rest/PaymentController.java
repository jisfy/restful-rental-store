package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Payment;
import com.chompchompfig.store.domain.PaymentId;
import com.chompchompfig.store.infrastructure.jpa.PaymentRepository;
import com.chompchompfig.store.application.VideoRentalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Api(value ="Video Rentals, Payments", description = "Payment of Rentals API", tags = "{5}")
@RestController
@RequestMapping("/store")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private VideoRentalService videoRentalService;
    @Autowired
    private PaymentResourceAssembler resourceAssembler;

    @ApiOperation(value = "Gets the list of all Payments in the ledger")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved")
    })
    @GetMapping(value = "/payments", produces = MediaTypes.HAL_JSON_VALUE)
    public Resources<PaymentResource> getPayments() {
        return new Resources(resourceAssembler.toResources(paymentRepository.findAll()));
    }

    @ApiOperation(value = "Gets a Payment from the ledger")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved"),
            @ApiResponse(code = 404, message = "The selected Payment does not exist")
    })
    @GetMapping(value = "/payment/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public PaymentResource getPayment(@PathVariable String id) {
        PaymentId paymentId = PaymentId.from(id);
        Optional<PaymentResource> paymentResource =
                paymentRepository.findById(paymentId).map(p -> resourceAssembler.toResource(p));
        return paymentResource.get();
    }

    @ApiOperation(value = "Performs a payment which is associated with a Rental", response = PaymentResource.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully performed"),
            @ApiResponse(code = 404, message = "The selected Payment does not exist, and can't be performed"),
            @ApiResponse(code = 405, message = "The selected Payment can't be performed due to the Rental current " +
                    "state. The Rental must be in AWAITING_PAYMENT or AWAITING_PAYMENT_OVERDUE state in order to" +
                    "proceed"),
            @ApiResponse(code = 409, message = "The selected Payment can't be performed due to the Rental current " +
                    "state. Probably some Films included in the Rental are no longer available to rent. You should " +
                    "either cancel or modify the Rental to get rid of those Films")
    })
    @PostMapping(value = "/payment/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public PaymentResource fulfillPayment(@PathVariable String id) {
        PaymentId paymentId = PaymentId.from(id);
        Payment paymentPerformed = videoRentalService.performPayment(paymentId);
        return resourceAssembler.toResource(paymentPerformed);
    }

}
