package pik.Client.Data;

import lombok.Getter;
import lombok.Setter;

public class PaymentMethod
{
    @Getter
    String id;

    @Getter @Setter
    double limit;

    @Getter
    double baseLimit;
    
    @Getter
    double discount;

    public PaymentMethod(String id, double limit, double discount)
    {
        this.id = id;
        this.limit = limit;
        this.discount = discount;
        this.baseLimit = limit;
    }


}
