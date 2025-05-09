package pik.Client.Data;

import java.util.ArrayList;

import lombok.Getter;

public class Orders
{
    @Getter
    String id;

    @Getter
    double value;

    @Getter
    ArrayList<String> promotions;
}
