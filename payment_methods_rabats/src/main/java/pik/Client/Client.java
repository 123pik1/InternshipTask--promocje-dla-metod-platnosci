package pik.Client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pik.Client.Data.PaymentMethod;

public class Client
{
    Map<String, PaymentMethod> paymentMethodsMap;


    public Client()
    {

    }

    private <T> ArrayList<T>  readFromJson(String filePath, Class<T> clazz)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ArrayList<T> read = objectMapper.readValue(new File(filePath), new TypeReference<ArrayList<T>>() {});
            return read;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void initPaymentMethods(String filePath)
    {
        ArrayList<PaymentMethod> paymentMethods = readFromJson(filePath, PaymentMethod.class);
        paymentMethodsMap = new HashMap<>();
        for (PaymentMethod paymentMethod : paymentMethods) {
            paymentMethodsMap.put(paymentMethod.getId(), paymentMethod);
        }
    }

    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.err.println("Not enough arguments");
            return;
        }

    }
}
