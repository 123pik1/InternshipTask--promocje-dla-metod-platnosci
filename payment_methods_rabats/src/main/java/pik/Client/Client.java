package pik.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import pik.Client.Data.Order;
import pik.Client.Data.PaymentMethod;
import pik.Exceptions.ImpossibleTransactionException;
import pik.Exceptions.LimitExceededException;

public class Client {
    Map<String, PaymentMethod> paymentMethodsMap;
    ArrayList<Order> orders;
    List<PaymentInstance> listOfPayments = new ArrayList<>();
    Map<String, Order> ordersMap;
    List<Order> notFinishedOrders = new ArrayList<>();

    int recursiveIn = 0;

    public Client() {
    }

    private <T> ArrayList<T> readFromJson(String filePath, Class<T> clazz) throws FileNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ArrayList<T> read = objectMapper.readValue(new File(filePath),
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
            // Maps JSON input to array of type from clazz variable
            return read;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            throw e;
        } catch (Exception e) {
            System.out.println("General exception");
            throw new RuntimeException(e);
        }
    }

    public void initPaymentMethods(String filePath) throws FileNotFoundException {
        ArrayList<PaymentMethod> paymentMethods;
        try {
            paymentMethods = readFromJson(filePath, PaymentMethod.class);
        } catch (FileNotFoundException e) {
            System.out.println("PaymentMethods file not found");
            throw e;
        }
        paymentMethodsMap = new HashMap<>();
        for (PaymentMethod paymentMethod : paymentMethods) {
            paymentMethod.actualizeBaseLimit(); // Sets base limit to current limit
            paymentMethodsMap.put(paymentMethod.getId(), paymentMethod); // Saves readed paymentMethods to Map
        }
    }

    public void initOrders(String filePath) throws FileNotFoundException {
        try {
            ordersMap = new HashMap<>();
            orders = readFromJson(filePath, Order.class);
            orders.sort((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
            for (Order order : orders) {
                ordersMap.put(order.getId(), order);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Orders file not found");
            throw e;
        }
    }

    private PaymentInstance partialPaymentQualification(Order order, PaymentMethod method, PaymentMethod points)
            throws ImpossibleTransactionException {

        PaymentInstance payment = new PaymentInstance();
        if (points.getLimit() >= order.getValue() * 0.1) {
            payment.setDiscountValue(order.getValue() * 0.1);
        }

        payment.setValue(order.getValue() - payment.getDiscountValue());
        payment.setPointsUsed(order.getValue() * 0.1);

        if (payment.getMoneyValue() > method.getLimit()) {
            throw new ImpossibleTransactionException();
        }
        payment.setMethodId(method.getId());
        payment.setOrderId(order.getId());

        return payment;
    }

    private PaymentInstance pointsPaymentQualification(Order order, PaymentMethod points)
            throws ImpossibleTransactionException {

        PaymentInstance payment = new PaymentInstance();
        payment.setDiscountValue(order.getValue() * points.getDiscount());
        payment.setValue(order.getValue() - payment.getDiscountValue());
        payment.setPointsUsed(order.getValue());
        if (payment.getValue() > points.getLimit()) {
            throw new ImpossibleTransactionException();
        }
        payment.setOrderId(order.getId());

        return payment;
    }

    private PaymentInstance cardPaymentQualification(Order order, PaymentMethod method)
            throws ImpossibleTransactionException {
        PaymentInstance payment = new PaymentInstance();
        double discount = 0;
        if (order.getPromotions() != null && order.getPromotions().contains(method.getId())) {
            discount = method.getDiscount();
        }
        payment.setDiscountValue(order.getValue() * discount);
        payment.setValue(order.getValue() - payment.getDiscountValue());

        if (payment.getValue() > method.getLimit()) {
            throw new ImpossibleTransactionException();
        }
        payment.setMethodId(method.getId());
        payment.setOrderId(order.getId());
        return payment;
    }

    private void iterateOverMethods(Order order, PaymentMethod points, List<PaymentInstance> paymentInstances) {
        if (order == null)
            return;
        for (PaymentMethod method : paymentMethodsMap.values()) {
            try {
                paymentInstances.add(cardPaymentQualification(order, method));
            } catch (ImpossibleTransactionException e) {
            }

            try {
                paymentInstances.add(partialPaymentQualification(order, method, points));
            } catch (ImpossibleTransactionException e) {
            }
        }
    }

    private PaymentInstance chooseBestPayment(List<PaymentInstance> listOfPayments) {
        if (listOfPayments.size() == 0) {
            System.out.println("no length"); // TODO not finished orders, not payments and higher
            return null;
        }
        PaymentInstance bestPayment = new PaymentInstance();// To differ by discounts ammount - base constructor sets
                                                            // it to 0
        listOfPayments.sort((o1, o2) -> PaymentInstance.comparePaymentInstances(o1, o2));
        return listOfPayments.get(0);
    }

    private void paymentVerification(PaymentInstance payment, PaymentMethod points) {
        if (payment == null || payment.getMethodId() == null || payment.getOrderId() == null)
            return;
        PaymentMethod method = paymentMethodsMap.get(payment.getMethodId());
        System.out.println(payment.getOrderId());
        try {
            // System.out.println(payment.getOrderId());
            if (!payment.getMethodId().equals("PUNKTY"))
                method.spend(payment.getMoneyValue());
            points.spend(payment.getPointsUsed());
            // recursiveIn = 0;
        } catch (LimitExceededException e) {

            try {
                if (payment.getPointsUsed() != 0) // if it is partial transaction
                {
                    // recursiveIn = 0;
                    // System.out.println(payment.getOrderId());
                    if (!payment.getMethodId().equals("PUNKTY"))
                    method.spend(payment.getValue(), points);
                } else {
                    method.spend(payment.getValue());
                    points.spend(payment.getPointsUsed());
                }
            } catch (Exception ex) {

                List<PaymentInstance> paymentsForOrder = new ArrayList<>();
                iterateOverMethods(ordersMap.get(payment.orderId), points, paymentsForOrder);
                recursiveIn += 1;
                if (!(recursiveIn >= 10)) {
                    if (paymentsForOrder.size() == 0)
                    {
                        System.out.println(payment.getOrderId() + " " +payment.getMethodId() + " points " + points.getLimit() + " method " + paymentMethodsMap.get(payment.getMethodId()).getLimit());
                        notFinishedOrders.add(ordersMap.get(payment.getOrderId()));
                    }
                    paymentVerification(chooseBestPayment(paymentsForOrder), points);
                }
            }
        }

    }

    public void simulation() {
        PaymentMethod points = paymentMethodsMap.get("PUNKTY"); // finds PUNKTY - special record

        for (Order order : orders) {
            List<PaymentInstance> paymentInstances = new ArrayList<>();
            try {
                paymentInstances.add(pointsPaymentQualification(order, points)); // if its possible add paymentInstance
                                                                                 // for PUNKTY
            } catch (Exception e) {
            }
            iterateOverMethods(order, points, paymentInstances);
            listOfPayments.add(chooseBestPayment(paymentInstances));
        }

        listOfPayments.sort((o1, o2) -> Double.compare(o2.getDiscountValue(), o1.getDiscountValue()));

        for (PaymentInstance payment : listOfPayments) {
            paymentVerification(payment, points);
        }

    }

    public String getSepndingsString() {
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

        try {
            client.initOrders(args[0]);// inits Orders with first adders inputed, if file not found end program
        } catch (Exception e) {
            return;
        }
        try {
            client.initPaymentMethods(args[1]); // inits PaymentMethods with second address inputed, if file not found
                                                // end program
        } catch (Exception e) {
            return;
        }

        client.simulation(); // starts simulation of buying process

        System.out.println(client.getSepndingsString()); // prints results of simulation
    }
}
