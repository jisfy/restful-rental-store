package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Payment;
import com.chompchompfig.store.domain.PaymentId;
import com.chompchompfig.store.domain.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel(description = "A representation of a Payment resource", value = "Payment")
@Relation(collectionRelation = "payments", value = "payment")
@JsonPropertyOrder({"paymentId", "status", "amount", "currency", "_links"})
public class PaymentResource extends ResourceSupport {

    private Payment payment;

    public PaymentResource(Payment payment) {
        this.payment = payment;
        this.add(linkTo(methodOn(PaymentController.class).getPayment(this.payment.getId().toString())).withSelfRel());
    }

    @ApiModelProperty(notes = "the Payment unique identifier")
    public PaymentId getPaymentId() {
        return this.payment.getId();
    }

    @ApiModelProperty(notes = "the Payment associated Currency code")
    public String getCurrency() {
        return this.payment.getCurrency();
    }

    @ApiModelProperty(notes = "the amount represented by the Payment")
    public Long getAmount() {
        return this.payment.getAmount();
    }

    @ApiModelProperty(notes = "the Payment status. Could be PENDING or DONE")
    public PaymentStatus getStatus() {
        return this.payment.getStatus();
    }

    @JsonIgnore
    Payment getPayment() {
        return this.payment;
    }
}
