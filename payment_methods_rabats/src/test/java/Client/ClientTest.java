package Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pik.Client.Data.Order;
import pik.Client.Data.PaymentMethod;
import pik.Client.Client;
import pik.Client.PaymentInstance;
import pik.Exceptions.ImpossibleTransactionException;
import pik.Exceptions.LimitExceededException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {

    private Client client;
    private PaymentMethod points;
    private Order order;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.paymentMethodsMap = new HashMap<>();
        points = new PaymentMethod("PUNKTY", 100.00, 15);
        client.paymentMethodsMap.put("PUNKTY", points);
        order = new Order();
        order.setId("ORDER1");
        order.setValue(100.00);
        client.ordersMap = new HashMap<>();
        client.ordersMap.put(order.getId(), order);
    }

    @Test
    void testPartialPaymentQualification() {
        PaymentMethod mZysk = new PaymentMethod("mZysk", 180.00, 0.10);
        client.paymentMethodsMap.put("mZysk", mZysk);
        try {
            PaymentInstance payment = client.partialPaymentQualification(order, mZysk, points);
            assertNotNull(payment);
            assertEquals("mZysk", payment.getMethodId());
            assertEquals(90.00, payment.getValue());
            assertEquals(10.00, payment.getPointsUsed());
        } catch (ImpossibleTransactionException e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void testPointsPaymentQualification() {
        try {
            PaymentInstance payment = client.pointsPaymentQualification(order, points);
            assertNotNull(payment);
            assertEquals(15.00, payment.getDiscountValue());
        } catch (ImpossibleTransactionException e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void testCardPaymentQualification() {
        PaymentMethod mZysk = new PaymentMethod("mZysk", 180.00, 10);
        client.paymentMethodsMap.put("mZysk", mZysk);
        order.setPromotions(new ArrayList<>(List.of("mZysk")));
        try {
            PaymentInstance payment = client.cardPaymentQualification(order, mZysk);
            assertNotNull(payment);
            assertEquals("mZysk", payment.getMethodId());
            assertEquals(10.00, payment.getDiscountValue());
            assertEquals(90.00, payment.getValue());

        } catch (ImpossibleTransactionException e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void testSpendPaymentMethod() {
        PaymentMethod testMethod = new PaymentMethod("TEST", 100.0, 0.0);
        try {
            testMethod.spend(50.0);
            assertEquals(50.0, testMethod.getLimit());
        } catch (LimitExceededException e) {
            fail("LimitExceededException should not be thrown");
        }

        assertThrows(LimitExceededException.class, () -> testMethod.spend(60.0));
    }

    @Test
    void testChooseBestPayment() {
        List<PaymentInstance> payments = new ArrayList<>();
        PaymentInstance payment1 = new PaymentInstance();
        payment1.setDiscountValue(20.0);
        PaymentInstance payment2 = new PaymentInstance();
        payment2.setDiscountValue(30.0);
        payments.add(payment1);
        payments.add(payment2);

        PaymentInstance bestPayment = client.chooseBestPayment(payments);
        assertEquals(30.0, bestPayment.getDiscountValue());
    }
}