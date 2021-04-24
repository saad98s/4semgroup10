package com.group11.demo.Domain;

import com.group11.demo.Persistence.PersistenceHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@ComponentScan({"com.group11.demo"})
@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")

public class API {

    public static SystemMain systemMain;
    public static OpcUaConnection opcUaConnection;
    public static IPersistenceHandler persistenceHandler = PersistenceHandler.getInstance();

    public static void main(String[] args) {
        //Creates the OPC UA Connection
        opcUaConnection = new OpcUaConnection("sim");
        //Created the SystemMain
        systemMain = new SystemMain(opcUaConnection);
        SpringApplication.run(API.class, args);
    }

    //Adds the current batch report to the db
    @GetMapping("/DB/addReport")
    public String addToDB() {
        this.scadaStopMonitoring();
        if (!systemMain.getScada().currentlyMonitoring) {
            persistenceHandler.addReport(systemMain.generateBatchReport());
            return "worked";
        } else {
            return "Attempted to add report, however system was still monitoring";
        }

    }

    @GetMapping("/DB/addDumReport")
    public String generateDummyReport() {

        ArrayList<Float> floatArray = new ArrayList<>();
        for (float i = 0; i < 1000; i++) {
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
        testList.set(2, (testList.get(2) + Double.valueOf(50)));
        testList.set(2, (testList.get(2) + Double.valueOf(5)));

        persistenceHandler.addReport(new BatchReport(886, 3, 100, 500, 490, 10, floatArray, floatArray, dummyErrorArray, testList));

        return "Dummy Added, look for Batch ID: 888";
    }

    //Reading from the cube exposed
    @GetMapping("/DB/removeBatch")
    public String removeBatchReportByID(@RequestParam(value = "batchID") float batchID) {
        persistenceHandler.removeReportByBatchID(batchID);
        return "batchReport with id:" + batchID + " has been deleted";
    }


    //Reading from the cube exposed
    @GetMapping("/read")
    public String readFromCube(@RequestParam(value = "enum", defaultValue = "STATE") NodeIDs node) {
        return opcUaConnection.read(node);
    }

    //MES functionality exposed
    @GetMapping("/MES/initProduction")
    public String initializeProduction(@RequestParam(value = "values", defaultValue = "Error!") String values) {

        String[] valueArray = new String[0];
        if (valueArray.length > 1) {
            Arrays.fill(valueArray, null);
        }
        valueArray = values.split(",");

        if (valueArray.length != 4) {
            return "Error!";
        } else {
            System.out.println(Arrays.toString(valueArray));    //Just to troubleshoot

            String batchID = valueArray[0];
            String productID = valueArray[1];
            String amountOfProducts = valueArray[2];
            String machineSpeed = valueArray[3];

            if (Integer.parseInt(productID) > 5 || Integer.parseInt(productID) < 0) {
                return "product ID: " + productID + " is invalid, has to be [0;5]";
            }

            switch (productID) {
                case "0":
                    if (Integer.parseInt(machineSpeed) < 0 || Integer.parseInt(machineSpeed) > 600) {
                        return "Invalid machine speed, valid arguments for product type: " + productID + " is [0;600]";
                    } else {
                        break;
                    }
                case "1":
                    if (Integer.parseInt(machineSpeed) < 0 || Integer.parseInt(machineSpeed) > 300) {
                        return "Invalid machine speed, valid arguments for product type: " + productID + " is [0;300]";
                    } else {
                        break;
                    }
                case "2":
                    if (Integer.parseInt(machineSpeed) < 0 || Integer.parseInt(machineSpeed) > 150) {
                        return "Invalid machine speed, valid arguments for product type: " + productID + " is [0;150]";
                    } else {
                        break;
                    }
                case "3":
                    if (Integer.parseInt(machineSpeed) < 0 || Integer.parseInt(machineSpeed) > 200) {
                        return "Invalid machine speed, valid arguments for product type: " + productID + " is [0;200]";
                    } else {
                        break;
                    }
                case "4":
                    if (Integer.parseInt(machineSpeed) < 0 || Integer.parseInt(machineSpeed) > 100) {
                        return "Invalid machine speed, valid arguments for product type: " + productID + " is [0;100]";
                    } else {
                        break;
                    }
                case "5":
                    if (Integer.parseInt(machineSpeed) < 0 || Integer.parseInt(machineSpeed) > 125) {
                        return "Invalid machine speed, valid arguments for product type: " + productID + " is [0;125]";
                    } else {
                        break;
                    }
            }


            systemMain.getMes().initializeProduction(batchID, productID, amountOfProducts, machineSpeed);

            return String.format("Production initialized with: BatchID: " + batchID + " ProductID: " + productID +
                    " Amount of products: " + amountOfProducts + " Machine Speed: " + machineSpeed);
        }
    }

    @GetMapping("/MES/start")
    public String mesStart() {
        String State = opcUaConnection.read(NodeIDs.STATE);
        switch (State) {
            case "9":
                systemMain.getMes().clear();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (opcUaConnection.read(NodeIDs.STATE).equals("2")) {
                    systemMain.getMes().reset();
                }
            case "2":
                systemMain.getMes().reset();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            case "17":
                systemMain.getMes().reset();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }


        systemMain.getMes().startProduction();

        this.scadaStartMonitoring(5000);
        return "Production started";
    }

    @GetMapping("/MES/stop")
    public String mesStop() {
        systemMain.getMes().stopProduction();
        if (systemMain.getScada().currentlyMonitoring) {
            this.scadaStopMonitoring();
        }
        return "Production stopped";
    }

    @GetMapping("/MES/abort")
    public String mesAbort() {
        systemMain.getMes().abort();
        return "Production aborted";
    }

    @GetMapping("/MES/clear")
    public String mesClear() {
        systemMain.getMes().clear();
        return "Production cleared";
    }

    //SCADA functionality exposed
    @GetMapping("/SCADA/start")
    public String scadaStartMonitoring(@RequestParam(value = "freq", defaultValue = "5000") int sleep) {
        if (systemMain.getScada().currentlyMonitoring) {
            return "Already monitoring";
        } else {
            systemMain.getScada().startMonitoring(sleep);
            return "Monitoring started";
        }
    }

    @GetMapping("/SCADA/stop")
    public String scadaStopMonitoring() {
        systemMain.getScada().stopMonitoring();
        return "Monitoring stopped";
    }

    @GetMapping("/SCADA/getAll")
    public String scadaPrintEverything() {
        return systemMain.getScada().printEverything();
    }

    @GetMapping("/SCADA/getUI")
    public String scadaUpdateUI() {
        return systemMain.getScada().fetchForUI();
    }

    //SystemMain functionality
    @GetMapping("/System/genBatchReport")
    public String generateBatchReport() {
        return systemMain.generateBatchReport().toString();
    }

    @GetMapping("/DB/getBatchReport")
    public String getBatchReport(@RequestParam(value = "batchID") float batchID) {
        return persistenceHandler.findByID(batchID).toString();
    }

}
