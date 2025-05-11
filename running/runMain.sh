cd ../payment_methods_rabats
mvn exec:java -Dexec.mainClass="pik.Client.Client" -Dexec.args="/home/pik/Desktop/workspaces/data/orders.json /home/pik/Desktop/workspaces/data/paymentmethods.json"