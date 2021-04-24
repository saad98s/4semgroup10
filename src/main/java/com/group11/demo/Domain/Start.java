package com.group11.demo.Domain;

import com.group11.demo.Persistence.PersistenceHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

public class Start {

    public static void main(String[] args) {
//        //Creates the OPC UA Connection
//        OpcUaConnection opcUaConnection = new OpcUaConnection();
//        //Created the SystemMain
//        SystemMain systemMain = new SystemMain(opcUaConnection);

        /* --- Testing --- */

//        //Reads the current machine state
//        System.out.println(opcUaConnection.read(NodeIDs.STATE));


//        //Subscribes to the CntrlCmd
//        opcUaConnection.subscribe(NodeIDs.PACKML_COMMAND);


//        //Sends an abort command
//        SystemMain.mes.abort();
//
//        //Sleeps for 5 seconds
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        //Sends a clear command
//        SystemMain.mes.clear();


//        //Starts monitoring (currently only temp and humidity)
//        systemMain.scada.startMonitoring(1000);
//
//        //Sleeps for 5 seconds
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        //Stops monitoring
//        systemMain.scada.stopMonitoring();


//        //Reads and prints all values from all nodes the program knows off
//        System.out.println(systemMain.scada.printEverything());

//        System.out.println(systemMain.getScada().fetchForUI());





//        System.out.println(persistenceHandler.getReports());

//        System.out.println(persistenceHandler.getBatchReports().get(0).toString());

//        IPersistenceHandler persistenceHandler = PersistenceHandler.getInstance();
//
//        ArrayList<BatchReport> batchReports = new ArrayList<>();
//        batchReports.add(BatchReport.getDummyReport());
//
//        persistenceHandler.addReports(batchReports);
//
//        System.out.println(persistenceHandler.getBatchReports().toString());
//
//        System.out.println(persistenceHandler.getBatchReports().get(0).getBatchID());
//
//        System.out.println(persistenceHandler.findByProductType(2));
//
//
//        for (BatchReport batchReport : persistenceHandler.getBatchReports()) {
//            System.out.println(batchReport);
//        }

        IPersistenceHandler persistenceHandler = PersistenceHandler.getInstance();

        ArrayList<Float> floatArray = new ArrayList<>();
        for (float i = 0; i < 5; i++) {
            floatArray.add(i);
        }

        ArrayList<String> dummyErrorArray = new ArrayList<>();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dummyErrorArray.add("Empty Inventory encountered at: " + timestamp);
        dummyErrorArray.add("Maintenance needed at: " + timestamp);
        dummyErrorArray.add("Manual stop encountered at: " + timestamp);
        dummyErrorArray.add("Motor power encountered at: " + timestamp);
        dummyErrorArray.add("Manual abort encountered at: " + timestamp);

        ArrayList<Double> testList = new ArrayList<>(Collections.nCopies(20, Double.valueOf(0)));
        testList.set(2, (testList.get(2) + Double.valueOf(5)));

        BatchReport testReport = new BatchReport(888, 0, 400, 2500, 400, 150, floatArray, floatArray, dummyErrorArray, testList);

        persistenceHandler.addReport(testReport);

        System.out.println("OEE: " + testReport.oee);

    }
}

