package pik.Client.Data;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class Order
{
    @Getter @Setter
    String id;

    @Getter @Setter
    double value;

    @Getter @Setter
    ArrayList<String> promotions;

    public Order() {
    }
}
