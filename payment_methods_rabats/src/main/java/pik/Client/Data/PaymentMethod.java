package pik.Client.Data;

import lombok.Getter;
import lombok.Setter;

public class PaymentMethod
{
    @Getter @Setter
    String id;

    @Getter @Setter
    double limit;

    @Getter @Setter
    double baseLimit;
    
    @Getter @Setter
    double discount;

    public PaymentMethod()
    {}

    public PaymentMethod(String id, double limit, double discount)
    {
        this.id = id;
        this.limit = limit;
        this.discount = discount;
        this.baseLimit = limit;
    }

    public void actualizeBaseLimit()
    {
        baseLimit = limit;
    }

    public double getSpent()
    {
        return baseLimit - limit;
    }

    public boolean isPunkty()
    {
        if (id.equals("PUNKTY"))
            return true;
        return false;
    }


}
