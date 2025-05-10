package pik.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import pik.Client.Data.Order;
import pik.Client.Data.PaymentMethod;

public class Client {
    Map<String, PaymentMethod> paymentMethodsMap;
    ArrayList<Order> orders;

    public Client() {
    }

    private <T> ArrayList<T> readFromJson(String filePath, Class<T> clazz) throws FileNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ArrayList<T> read = objectMapper.readValue(new File(filePath),
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
            //Maps JSON input to array of type from clazz variable
            return read;
        } catch (FileNotFoundException e) {
            System.out.println("File not found"); //TODO fix exceptions
            throw e;
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal argument");
            throw e;
        } catch (NullPointerException e) {
            System.out.println("Null pointer");
            throw e;
        }
        catch (IOException e) {
            System.out.println("IO exception");
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("General exception");
            throw new RuntimeException(e);
        }
    }

    public boolean initPaymentMethods(String filePath) {
        ArrayList<PaymentMethod> paymentMethods;
        try {
            paymentMethods = readFromJson(filePath, PaymentMethod.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("PaymentMethods file not found");
            return false;
        }
        paymentMethodsMap = new HashMap<>();
        for (PaymentMethod paymentMethod : paymentMethods) {
            paymentMethod.actualizeBaseLimit(); // Sets base limit to current limit
            paymentMethodsMap.put(paymentMethod.getId(), paymentMethod); // Saves readed paymentMethods to Map
        }
        return true;
    }

    public boolean initOrders(String filePath) {
        try {
            orders = readFromJson(filePath, Order.class);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Orders file not found");
            return false;
        }
    }


    public void simulation()
    {
        for (Order orders2 : orders) {
            
        }
    }

    public String getSepndingsString()
    {
        String str = "";
        for (PaymentMethod method : paymentMethodsMap.values()) {
            str += method.getId();
            str += " ";
            str += method.getSpent();
            str += "\n"; // parts of string divided for readability
        }

        return str;
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Not enough arguments");
            return;
        }
        Client client = new Client();

        if (!client.initOrders(args[0])) //TODO change to try/catch?
            return; // inits Orders with first adders inputed, if file not found end program
        if (!client.initPaymentMethods(args[1]))
            return; // inits PaymentMethods with second address inputed, if file not found end program

        client.simulation(); // starts simulation of buying process
        System.out.println(client.getSepndingsString()); // prints results of simulation
    }
}
