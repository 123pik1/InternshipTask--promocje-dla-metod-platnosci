package pik.Client.Data;

import lombok.Getter;
import lombok.Setter;
import pik.Exceptions.LimitExceededException;

public class PaymentMethod
{
    @Getter @Setter
    String id;

    @Getter @Setter
    double limit;

    @Getter
    double baseLimit;
    
    @Setter
    double discount;

    public PaymentMethod()
    {}

    public PaymentMethod(PaymentMethod method)
    {
        this.baseLimit = method.getBaseLimit();
        this.limit = method.getLimit();
        this.id = method.getId();
        this.discount = method.getDiscount();
    }

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

    public void setBase()
    {
        this.baseLimit = limit;
    }

    public boolean isPunkty()
    {
        if (id.equals("PUNKTY"))
            return true;
        return false;
    }

    public void spend(double spending) throws LimitExceededException
    {
        if (limit > spending) {
            limit -= spending;
            System.out.println(id+" spent: "+spending +" left "+this.limit);
        } else {
            throw new LimitExceededException();
        }
    }
    
    public void spend(double spending, PaymentMethod points) throws LimitExceededException
    {
        System.out.println("special spending " + id);
        points.spend(spending - this.limit);
        this.limit -= spending;
        if (this.limit <=0)
            this.limit = 0;
    }

    public double getDiscount()
    {
        return discount / 100; // to do discount fraction of price
    }

}
