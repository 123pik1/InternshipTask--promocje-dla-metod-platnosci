package pik.Client;

import lombok.Getter;
import lombok.Setter;

public class PaymentInstance {
    @Getter
    @Setter
    double pointsUsed;

    @Getter
    @Setter
    double value; // How much it actually cost in money

    @Getter
    @Setter
    double discountValue; // value of discount

    @Getter
    @Setter
    String methodId;

    @Getter
    @Setter
    String orderId;

    public PaymentInstance() {
        this.discountValue = 0;
        this.pointsUsed = 0;
        this.value = 0;
        this.methodId = "PUNKTY"; // in default Payment is in PUNKTY
    }

    public PaymentInstance(double pointsUsed, double value, double promotion) {
        this.pointsUsed = pointsUsed;
        this.value = value;
        this.discountValue = promotion;
    }

    public double getMoneyValue() {
        return value - pointsUsed;
    }

    public static int comparePaymentInstances(PaymentInstance o1, PaymentInstance o2)
    {
        int discountComparison = Double.compare(o2.getDiscountValue(), o1.getDiscountValue());
        if (discountComparison == 0) {
            return Double.compare(o2.getPointsUsed(), o1.getPointsUsed());
        }
        return discountComparison;
    }
}
