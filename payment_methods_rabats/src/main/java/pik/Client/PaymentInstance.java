package pik.Client;

import lombok.Getter;

public class PaymentInstance
{
    @Getter
    double pointsUsed;
    @Getter
    double value;
    @Getter
    double promotion;

    public PaymentInstance()
    {
        this.promotion = 0;
    }

    public PaymentInstance(double pointsUsed, double value, double promotion)
    {
        this.pointsUsed = pointsUsed;
        this.value = value;
        this.promotion = promotion;
    }
}
