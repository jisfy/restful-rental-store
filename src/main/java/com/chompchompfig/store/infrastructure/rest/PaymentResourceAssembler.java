package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Payment;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PaymentResourceAssembler extends ResourceAssemblerSupport<Payment, PaymentResource> {

    public PaymentResourceAssembler() {
        super(PaymentController.class, PaymentResource.class);
    }

    @Override
    public PaymentResource toResource(Payment payment) {
        return new PaymentResource(payment);
    }
}



