package com.group11.demo.Domain;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;

public class BatchReport {

    @BsonProperty("_id")
    float batchID; //don't ask me!?
    float productType;
    float productionSpeed;
    float totalPlannedProducts;
    int productsProduced;
    int acceptedProducts;
    int defectiveProducts;

    double oee;

    private ArrayList<Float> temperature;
    private ArrayList<Float> humidity;
    private ArrayList<String> errorLogs;
    private ArrayList<Double> timeSpentInStates;

    public BatchReport(float batchID, float productType, float productionSpeed, float totalPlannedProducts, int productsProduced, int defectiveProducts, ArrayList<Float> temperature, ArrayList<Float> humidity, ArrayList<String> errorLogs, ArrayList<Double> timeSpentInStates) {
        this.batchID = batchID;
        this.productType = productType;
        this.productionSpeed = productionSpeed;
        this.totalPlannedProducts = totalPlannedProducts;
        this.productsProduced = productsProduced;
        this.acceptedProducts = productsProduced - defectiveProducts;
        this.defectiveProducts = defectiveProducts;
        this.temperature = temperature;
        this.humidity = humidity;
        this.errorLogs = errorLogs;
        this.timeSpentInStates = timeSpentInStates;
        oee = calculateOEE();
    }

    //Getters and setters for MongoDB implementation

    public int getProductsProduced() {
        return productsProduced;
    }

    public void setProductsProduced(int productsProduced) {
        this.productsProduced = productsProduced;
    }

    public double getOee() {
        return oee;
    }

    public void setOee(double oee) {
        this.oee = oee;
    }

    public BatchReport() {
    }

    public float getBatchID() {
        return batchID;
    }

    public void setBatchID(float batchID) {
        this.batchID = batchID;
    }

    public float getProductType() {
        return productType;
    }

    public void setProductType(float productType) {
        this.productType = productType;
    }

    public float getProductionSpeed() {
        return productionSpeed;
    }

    public void setProductionSpeed(float productionSpeed) {
        this.productionSpeed = productionSpeed;
    }

    public float getTotalPlannedProducts() {
        return totalPlannedProducts;
    }

    public void setTotalPlannedProducts(float totalPlannedProducts) {
        this.totalPlannedProducts = totalPlannedProducts;
    }

    public int getAcceptedProducts() {
        return acceptedProducts;
    }

    public void setAcceptedProducts(int acceptedProducts) {
        this.acceptedProducts = acceptedProducts;
    }

    public int getDefectiveProducts() {
        return defectiveProducts;
    }

    public void setDefectiveProducts(int defectiveProducts) {
        this.defectiveProducts = defectiveProducts;
    }

    public ArrayList<Float> getTemperature() {
        return temperature;
    }

    public void setTemperature(ArrayList<Float> temperature) {
        this.temperature = temperature;
    }

    public ArrayList<Float> getHumidity() {
        return humidity;
    }

    public void setHumidity(ArrayList<Float> humidity) {
        this.humidity = humidity;
    }

    public ArrayList<String> getErrorLogs() {
        return errorLogs;
    }

    public void setErrorLogs(ArrayList<String> errorLogs) {
        this.errorLogs = errorLogs;
    }

    public ArrayList<Double> getTimeSpentInStates() {
        return timeSpentInStates;
    }

    public void setTimeSpentInStates(ArrayList<Double> timeSpentInStates) {
        this.timeSpentInStates = timeSpentInStates;
    }

    public double calculateOEE() {
        //Availability = runtime / planned production time
        //where runtime = planned_production_time - downtime

        double planned_production_time = totalPlannedProducts / productionSpeed;

        double downtimeInSeconds = 0;

        for (int i = 0; i < timeSpentInStates.size(); i++) {
            if (i == 6) {
                continue;
            }
            downtimeInSeconds += timeSpentInStates.get(i);
        }

        double downtime = downtimeInSeconds / 60;

        double runtime = planned_production_time - downtime;

        double availability = runtime / planned_production_time;

        //System.out.println("Availability = " + runtime + "/" + planned_production_time + " = " + availability);


        //Performance = (Ideal Cycle Time * total products)/runtime
        double ideal_cycle_time = 0;

        int test = (int) productType;

        switch (test) {
            case 0:
                ideal_cycle_time = 1d / 600d;
                break;
            case 1:
                ideal_cycle_time = 1d / 300d;
                break;
            case 2:
                ideal_cycle_time = 1d / 150d;
                break;
            case 3:
                ideal_cycle_time = 1d / 200d;
                break;
            case 4:
                ideal_cycle_time = 1d / 100d;
                break;
            case 5:
                ideal_cycle_time = 1d / 125d;
                break;
        }

        double performance = (ideal_cycle_time * totalPlannedProducts) / runtime;

        //System.out.println("Performance = (" + ideal_cycle_time + "*" + totalPlannedProducts + ")/" + runtime + " = " + performance);

        //Quality = good_products / total_products
        double quality = acceptedProducts / totalPlannedProducts; //this should maybe be the amount of products products at a any given time...
        //System.out.println("Quality = " + quality);

        // OEE = Availability * Performance * Quality

        return (availability * performance * quality);
        //return (acceptedProducts * ideal_cycle_time) / planned_production_time;

    }

//    public static BatchReport getDummyReport(){     //Useful for testing
//        ArrayList<Float> floatArray = new ArrayList<>();
//        for (float i = 0; i < 5; i++) {
//            floatArray.add(i);
//        }
//
//        ArrayList<String> dummyErrorArray = new ArrayList<>();
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//        dummyErrorArray.add("Empty Inventory encountered at: " + timestamp);
//        dummyErrorArray.add("Maintenance needed at: " + timestamp);
//        dummyErrorArray.add("Manual stop encountered at: " + timestamp);
//        dummyErrorArray.add("Motor power encountered at: " + timestamp);
//        dummyErrorArray.add("Manual abort encountered at: " + timestamp);
//
//        ArrayList<Double> doubleArrayList = new ArrayList<>(Collections.nCopies(6, Double.valueOf(0)));
//        doubleArrayList.set(2,(doubleArrayList.get(2)+Double.valueOf(50)));
//        doubleArrayList.set(2,(doubleArrayList.get(2)+Double.valueOf(5)));
//
//        return new BatchReport(888, 2, 150, 50, 40, 10, floatArray, floatArray, dummyErrorArray,doubleArrayList);
//    }

//    @Override
//    public String toString() {
//        return new Gson().toJson(this);
//    }

    @Override
    public String toString() {
        return String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                batchID, productType, productionSpeed, totalPlannedProducts, acceptedProducts, defectiveProducts, oee, timeSpentInStates, temperature, humidity, errorLogs);
    }

}
