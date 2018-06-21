package com.chompchompfig.store.domain;

import com.chompchompfig.store.application.VideoRentalService;
import com.chompchompfig.store.infrastructure.jpa.PaymentRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoRentalServiceTests {

    @Autowired
    private VideoRentalService videoRentalService;
    private SimpleFixtureFactory fixtureFactory = new SimpleFixtureFactory();
    @MockBean
    private PaymentRepository paymentRepository;

    @Test
    public void performPaymentWithPendingPaymentAndAwaitingPaymentRentalShouldAddBonusPointsChangeRentalToPayment() {
        long expectedBonusPointsInCard = 4;
        performPaymentWithPaymentStatusAndRentalStatusShouldModifyStatusAndPoints(PaymentStatus.PENDING,
                RentalStatus.AWAITING_PAYMENT, expectedBonusPointsInCard, PaymentStatus.DONE, RentalStatus.PAID);
    }

    @Test
    public void performPaymentWithPendingPaymentAndAwaitingPaymentOverdueRentalRentalShouldReturnAllFilms() {
        long expectedBonusPointsInCard = 0;
        performPaymentWithPaymentStatusAndRentalStatusShouldModifyStatusAndPoints(PaymentStatus.PENDING,
                RentalStatus.AWAITING_PAYMENT_OVERDUE, expectedBonusPointsInCard, PaymentStatus.DONE,
                    RentalStatus.RETURNED);
    }

    @Test
    public void performPaymentWithDonePaymentShouldDoNothing() {
        performPaymentWithPaymentStatusAndRentalStatusShouldModifyStatusAndPoints(PaymentStatus.DONE,
                RentalStatus.PAID, 0, PaymentStatus.DONE, RentalStatus.PAID);
    }

    @Test(expected = IllegalStateException.class)
    public void performPaymentWithPendingPaymentAndCanceledRentalShouldThrowException() {
        performPaymentWithPaymentStatusAndRentalStatusShouldModifyStatusAndPoints(PaymentStatus.PENDING,
                RentalStatus.CANCELLED, 0, PaymentStatus.DONE, RentalStatus.PAID);
    }

    @Test(expected = IllegalStateException.class)
    public void performPaymentWithPendingPaymentAndReturnedRentalShouldThrowException() {
        performPaymentWithPaymentStatusAndRentalStatusShouldModifyStatusAndPoints(PaymentStatus.PENDING,
                RentalStatus.RETURNED, 0, PaymentStatus.DONE, RentalStatus.PAID);
    }

    @Test(expected = IllegalStateException.class)
    public void performPaymentWithPendingPaymentAndPaidRentalShouldThrowException() {
        performPaymentWithPaymentStatusAndRentalStatusShouldModifyStatusAndPoints(PaymentStatus.PENDING,
                RentalStatus.PAID, 0, PaymentStatus.DONE, RentalStatus.PAID);
    }

    public void performPaymentWithPaymentStatusAndRentalStatusShouldModifyStatusAndPoints(
            PaymentStatus sourcePaymentStatus, RentalStatus sourceRentalStatus, long expectedPointsInCard,
            PaymentStatus expectedPaymentStatus, RentalStatus expectedRentalStatus) {
        Rental someRental = fixtureFactory.newRentalWithItems();
        Assert.assertNotNull(someRental);
        someRental.setStatus(sourceRentalStatus);
        Customer customer = someRental.getCustomer();
        Assert.assertNotNull(customer);
        Assert.assertEquals(Long.valueOf(0l), customer.getPointsInCard());

        Payment payment = someRental.getPendingPayment();
        payment.setStatus(sourcePaymentStatus);
        Assert.assertNotNull(payment);
        Mockito.when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        videoRentalService.performPayment(payment.getId());

        Assert.assertEquals(expectedRentalStatus, someRental.getStatus());
        Assert.assertEquals(expectedPaymentStatus, payment.getStatus());
        Assert.assertEquals(Long.valueOf(expectedPointsInCard), customer.getPointsInCard());
    }
}
