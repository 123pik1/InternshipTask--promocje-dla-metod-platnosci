# Payment Methods and Promotions

This project simulates a payment processing system with support for different payment methods and promotions. It reads order and payment method data from JSON files, applies promotions, and calculates the best payment option for each order.

## Project Structure

Client
----| Data
-------| Order
-------| PaymentMethod
----| Client
----| PaymentInstance
Exceptions
----| ImpossibleTransactionException
----| LimitExceededException
----| NoPromotionsException


## Dependencies

*   [Lombok](https://projectlombok.org/):  Used for automatic generation of boilerplate code (getters, setters, constructors, etc.).
*   [Jackson Databind](https://github.com/FasterXML/jackson-databind): Used for reading and writing JSON data.
*   [JUnit 5](https://junit.org/junit5/): Used for unit testing.

## Usage

1.  **Clone the repository:**

    ```sh
    git clone https://github.com/123pik1/InternshipTask--promocje-dla-metod-platnosci.git
    ```

2.  **Build the project using Maven:**

    ```sh
    cd payment_methods_rabats
    mvn clean install
    ```

3.  **Run the simulation:**

    The main class is `pik.Client.Client`. It takes two command-line arguments:

    *   Path to the orders JSON file (`orders.json`)
    *   Path to the payment methods JSON file (`paymentmethods.json`)

    You can use the provided `runMain.sh` script in the `running/` directory as a template.  Modify the paths to point to your `orders.json` and `paymentmethods.json` files (located in `Zadanie2025v2/`).

    Example:

    ```sh
    java -cp target/classes:target/dependency/* pik.Client.Client Zadanie2025v2/orders.json Zadanie2025v2/paymentmethods.json
    ```

    Or, using the provided script (after adjusting the paths):

    ```sh
    cd running
    ./runMain.sh
    ```

4.  **View the results:**

    The simulation will print the spending for each payment method to the console.

## Input Files

The project reads data from two JSON files:

*   **orders.json:** Contains a list of orders. Each order has an `id`, `value`, and an optional list of `promotions`.

    ```json
    [
      {
        "id": "ORDER1",
        "value": 100.00,
        "promotions": ["mZysk"]
      },
      {
        "id": "ORDER2",
        "value": 200.00
      }
    ]
    ```

*   **paymentmethods.json:** Contains a list of payment methods. Each payment method has an `id`, `limit`, and `discount`.

    ```json
    [
      {
        "id": "mZysk",
        "limit": 180.00,
        "discount": 10
      },
      {
        "id": "PUNKTY",
        "limit": 100.00,
        "discount": 15
      }
    ]
    ```

## Testing

The project includes unit tests for the `Client` and `PaymentMethod` classes. To run the tests, use the following Maven command:

```sh
mvn test
```