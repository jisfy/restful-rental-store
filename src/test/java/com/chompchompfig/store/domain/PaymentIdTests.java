package com.chompchompfig.store.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PaymentIdTests {

    public static final Long VALID_CUSTOMER_ID_PART = 123l;
    public static final Long VALID_RENTAL_ID_PART = 1l;
    public static final Long VALID_ID_PART = 1858735057263169l;
    public static final String VALID_INPUT_PAYMENT_ID = VALID_CUSTOMER_ID_PART + "." + VALID_RENTAL_ID_PART +
            "." + VALID_ID_PART; //"1.1858735057263169";
    public static final PaymentId VALID_PAYMENT_ID =
            new PaymentId(VALID_CUSTOMER_ID_PART, VALID_RENTAL_ID_PART, VALID_ID_PART);
    public static final String INVALID_INPUT_PAYMENT_ID_NO_DOT = "11858735057263169";
    public static final String INVALID_INPUT_PAYMENT_ID_TOO_LONG = "1.185873.50572631.69";

    @Test
    public void fromShouldReturnCorrectPaymentIdWithValidInput() {
        PaymentId paymentId = PaymentId.from(VALID_INPUT_PAYMENT_ID);
        Assert.assertNotNull(paymentId);
        Assert.assertEquals(VALID_PAYMENT_ID, paymentId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromShouldThrowExceptionWithInValidInputNoDot() {
        PaymentId.from(INVALID_INPUT_PAYMENT_ID_NO_DOT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromShouldThrowExceptionWithInValidInputTooLong() {
        PaymentId.from(INVALID_INPUT_PAYMENT_ID_TOO_LONG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromShouldThrowExceptionWithInValidInputNull() {
        PaymentId.from(null);
    }
}
