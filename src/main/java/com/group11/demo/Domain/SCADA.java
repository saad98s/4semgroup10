package com.group11.demo.Domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SCADA {

    OpcUaConnection opcUaConnection;    //Can we do think of any other way?

    ArrayList<Float> humidity;
    ArrayList<Float> temperature;
    ArrayList<Double> timeSpentInStates;
    //states are 1-5, thinking we can do all unknown states ie. 9? in index 0?
    ArrayList<String> errorLog;
    float totalProducts;
    int productsProduced;
    int defectiveProducts;
    float productionSpeed;
    float batchID; //dont ask me!?
    int state; //isn't this included in the time spent in states?
    float productType;
    boolean currentlyMonitoring;

    public boolean isCurrentlyMonitoring() {
        return currentlyMonitoring;
    }

    List<Thread> threadList = new ArrayList<>();

    public SCADA(OpcUaConnection opcUaConnection) {
        this.opcUaConnection = opcUaConnection;
        currentlyMonitoring = false;
    }

    public void initializeNewBatch() {
        humidity = new ArrayList<Float>();
        temperature = new ArrayList<Float>();
        timeSpentInStates = new ArrayList<>(Collections.nCopies(20, Double.valueOf(0)));
        errorLog = new ArrayList<String>();
        productsProduced = 0;
        defectiveProducts = 0;


        //Reading production values for current batch
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.batchID = Float.valueOf(opcUaConnection.read(NodeIDs.CURRENT_BATCH_ID));
        this.productionSpeed = Float.valueOf(opcUaConnection.read(NodeIDs.MACH_SPEED_U));
        this.productType = Float.valueOf(opcUaConnection.read(NodeIDs.PRODUCT_ID));
        this.totalProducts = Float.valueOf(opcUaConnection.read(NodeIDs.AMOUNT_OF_PRODUCTS_IN_BATCH));
        //System.out.println("BatchID = "+ this.batchID + ", speed = "+ this.productionSpeed + ", productType = "+ this.productType + ", totalProducts = "+ this.totalProducts );
    }

    public void startMonitoring(int sleep_in_millis) {  //we have to decide whether to start this BEFORE the mes.start is run or just after (this has an impact on whether we should load the values for current batch or next batch...)
        currentlyMonitoring = true;

        //Clearing the records before starting new recordings
        initializeNewBatch();

        Thread temperatureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!opcUaConnection.read(NodeIDs.STATE).equals("17")) {
                    System.out.println("temperature is: " + opcUaConnection.read(NodeIDs.TEMP));
                    temperature.add(Float.valueOf(opcUaConnection.read(NodeIDs.TEMP)));

                    try {
                        Thread.sleep(sleep_in_millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadList.add(temperatureThread);

        Thread humidityThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!opcUaConnection.read(NodeIDs.STATE).equals("17")) {
                    System.out.println("humidity is: " + opcUaConnection.read(NodeIDs.RELATIVE_HUMIDITY));
                    humidity.add(Float.valueOf(opcUaConnection.read(NodeIDs.RELATIVE_HUMIDITY)));
                    try {
                        Thread.sleep(sleep_in_millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadList.add(humidityThread);

        Thread stateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!opcUaConnection.read(NodeIDs.STATE).equals("17")) {
                    System.out.println("state is: " + opcUaConnection.read(NodeIDs.STATE));
                    int state = Integer.parseInt(opcUaConnection.read(NodeIDs.STATE));
                    System.out.println("Reading every " + sleep_in_millis / 1000 + " seconds (hint: http://localhost:8080/SCADA/start?freq=millis)");

                    timeSpentInStates.set(state, (timeSpentInStates.get(state) + (double) (sleep_in_millis / (1000)))); //Currently it shows seconds in states, can do minutes or w/e
                    try {
                        Thread.sleep(sleep_in_millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadList.add(stateThread);

        Thread errorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int oldReason = 0;
                while (!opcUaConnection.read(NodeIDs.STATE).equals("17")) {
                    int stopReason = Integer.parseInt(opcUaConnection.read(NodeIDs.STOP_REASON_VALUE));
                    if (stopReason >= 10 && stopReason <= 14) {
                        System.out.println("Stop reason is: " + stopReason);
                    }
                    if (stopReason != oldReason) {
                        if (stopReason >= 10 && stopReason <= 14) {
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            switch (stopReason) {
                                case 10:
                                    errorLog.add("Empty Inventory encountered at: " + timestamp);
                                    break;
                                case 11:
                                    errorLog.add("Maintenance needed at: " + timestamp);
                                    break;
                                case 12:
                                    errorLog.add("Manual stop encountered at: " + timestamp);
                                    break;
                                case 13:
                                    errorLog.add("Motor power encountered at: " + timestamp);
                                    break;
                                case 14:
                                    errorLog.add("Manual abort encountered at: " + timestamp);
                                    break;
                            }
                        }
                    }
                    oldReason = stopReason;

                    try {
                        Thread.sleep(sleep_in_millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadList.add(errorThread);


        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).start();
        }
    }

    public void stopMonitoring() {
        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).stop();
        }

        //we create new threads when start production is called (this is the current implementation so that the threads can have a variable sleep timer)
        threadList.clear();

        currentlyMonitoring = false;

        this.defectiveProducts = Integer.parseInt(opcUaConnection.read(NodeIDs.PRODUCTS_DEFECTIVE));
        this.productsProduced = Integer.parseInt(opcUaConnection.read(NodeIDs.PRODUCTS_PROCESSED));
    }

    public String fetchForUI() {
        String state = opcUaConnection.read(NodeIDs.STATE) + ",";
        String batchID = opcUaConnection.read(NodeIDs.CURRENT_BATCH_ID) + ",";
        String productID = opcUaConnection.read(NodeIDs.PRODUCT_ID) + ",";
        String amountOfProducts = opcUaConnection.read(NodeIDs.AMOUNT_OF_PRODUCTS_IN_BATCH) + ",";
        String productsProduced = opcUaConnection.read(NodeIDs.PRODUCTS_PROCESSED) + ",";
        String defectiveProducts = opcUaConnection.read(NodeIDs.PRODUCTS_DEFECTIVE) + ",";
        String currentHumidity = opcUaConnection.read(NodeIDs.RELATIVE_HUMIDITY) + ",";
        String currentTemperature = opcUaConnection.read(NodeIDs.TEMP) + ",";
        String barley = opcUaConnection.read(NodeIDs.INV_BARLEY) + ",";
        String malt = opcUaConnection.read(NodeIDs.INV_MALT) + ",";
        String hops = opcUaConnection.read(NodeIDs.INV_HOPS) + ",";
        String wheat = opcUaConnection.read(NodeIDs.INV_WHEAT) + ",";
        String yeast = opcUaConnection.read(NodeIDs.INV_YEAST) + ",";
        String vibration = opcUaConnection.read(NodeIDs.VIBRATION) + ",";
        String maintenance = opcUaConnection.read(NodeIDs.MAINTENANCE_COUNTER) + ",";
        String machineSpeed = opcUaConnection.read(NodeIDs.MACH_SPEED_U);

        return "" + state + batchID + productID + amountOfProducts + productsProduced + defectiveProducts + currentHumidity + currentTemperature + barley + malt + hops + wheat + yeast + vibration + maintenance + machineSpeed;
    }

    public String printEverything() {  //For troubleshooting
        String returnString = "";
        for (NodeIDs node : NodeIDs.values()) {
            returnString += node.name() + " = " + opcUaConnection.read(node) + "<br>";
        }
        return returnString;
    }
}
