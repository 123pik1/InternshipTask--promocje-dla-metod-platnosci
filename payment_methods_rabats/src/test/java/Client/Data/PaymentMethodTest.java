package Client.Data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pik.Client.Data.PaymentMethod;
import pik.Exceptions.LimitExceededException;

public class PaymentMethodTest {

    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {
        paymentMethod = new PaymentMethod("PUNKTY", 150.0, 15.0);
    }

    @Test
    void testGetId() {
        assertEquals("PUNKTY", paymentMethod.getId());
    }

    @Test
    void testGetLimit() {
        assertEquals(150.0, paymentMethod.getLimit());
    }

    @Test
    void testGetDiscount() {
        assertEquals(0.15, paymentMethod.getDiscount());
    }

    @Test
    void testSpendSufficientFunds() {
        try {
            paymentMethod.spend(50.0);
            assertEquals(100.0, paymentMethod.getLimit());
        } catch (LimitExceededException e) {
            fail("Should not throw LimitExceededException");
        }
    }

    @Test
    void testSpendInsufficientFunds() {
        assertThrows(LimitExceededException.class, () -> paymentMethod.spend(200.0));
    }

    @Test
    void testGetSpent() {
        paymentMethod.actualizeBaseLimit();
        try {
            paymentMethod.spend(50.0);
        } catch (LimitExceededException e) {
            fail("Should not throw LimitExceededException");
        }
        assertEquals(50.0, paymentMethod.getSpent());
    }

    @Test
    void testIsPunkty() {
        assertTrue(paymentMethod.isPunkty());
        PaymentMethod testMethod = new PaymentMethod("TEST", 100.0, 0.0);
        assertFalse(testMethod.isPunkty());
    }

    @Test
    void testActualizeBaseLimit() {
        try {
            paymentMethod.spend(50.0);
        } catch (LimitExceededException e) {
            fail("Should not throw LimitExceededException");
        }
        paymentMethod.actualizeBaseLimit();
        assertEquals(100.0, paymentMethod.getBaseLimit());
    }

    @Test
    void testConstructorCopy() {
        PaymentMethod newMethod = new PaymentMethod(paymentMethod);
        assertEquals(paymentMethod.getBaseLimit(), newMethod.getBaseLimit());
        assertEquals(paymentMethod.getLimit(), newMethod.getLimit());
        assertEquals(paymentMethod.getId(), newMethod.getId());
        assertEquals(paymentMethod.getDiscount(), newMethod.getDiscount());
    }
}