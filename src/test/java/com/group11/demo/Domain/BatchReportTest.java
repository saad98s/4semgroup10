package com.group11.demo.Domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BatchReportTest {

    BatchReport testReport;

    @BeforeEach
    void setUp() {
        ArrayList<Float> temperatureArray = new ArrayList<>();
        for (float i = 0; i < 5; i++) {
            temperatureArray.add(17+i);
        }

        ArrayList<Float> humidArray = new ArrayList<>();
        for (float i = 0; i < 5; i++) {
            humidArray.add(i/10);
        }

        ArrayList<String> dummyErrorArray = new ArrayList<>();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dummyErrorArray.add("Empty Inventory encountered at: " + timestamp);
        dummyErrorArray.add("Maintenance needed at: " + timestamp);
        dummyErrorArray.add("Manual stop encountered at: " + timestamp);
        dummyErrorArray.add("Motor power encountered at: " + timestamp);
        dummyErrorArray.add("Manual abort encountered at: " + timestamp);

        ArrayList<Double> testList = new ArrayList<>(Collections.nCopies(20, Double.valueOf(0)));
        testList.set(6, (testList.get(6) + Double.valueOf(135)));


        testReport = new BatchReport(123,4,93,200,200,73,temperatureArray,humidArray,dummyErrorArray,testList);
    }

    @Test
    void getBatchID() {
        float actual = testReport.getBatchID();
        float expected = 123;

        assertEquals(actual,expected);
    }

    @Test
    void getProductType() {
        float actual = testReport.getProductType();
        float expected = 4;

        assertEquals(actual,expected);
    }

    @Test
    void getProductionSpeed() {
        float actual = testReport.getProductionSpeed();
        float expected = 93;

        assertEquals(actual,expected);
    }

    @Test
    void getTotalPlannedProducts() {
        float actual = testReport.getTotalPlannedProducts();
        float expected = 200;

        assertEquals(actual,expected);
    }

    @Test
    void getAcceptedProducts() {
        float actual = testReport.getAcceptedProducts();
        float productsProduced = 500;
        float defectiveProducts = 373;
        float expected = productsProduced-defectiveProducts;

        assertEquals(actual,expected);
    }

    @Test
    void getDefectiveProducts() {
        float actual = testReport.getDefectiveProducts();
        float expected = 73;

        assertEquals(actual,expected);

    }

    @Test
    void getTemperature() {
        ArrayList<Float> actual = testReport.getTemperature();

        for (int i = 0; i < actual.size(); i++) {
            assertEquals(actual.get(i),17+i);
        }

    }

    @Test
    void getHumidity() {
        ArrayList<Float> actual = testReport.getHumidity();

        for (float i = 0; i < actual.size(); i++) {
            assertEquals(actual.get((int)i),i/10);
        }
    }

    @Test
    void getErrorLogs() {
    //Not available for testing
    }

    @Test
    void getTimeSpentInStates() {
        ArrayList<Double> actual = testReport.getTimeSpentInStates();
        ArrayList<Double> expected = new ArrayList<>(Collections.nCopies(20, Double.valueOf(0)));
        expected.set(6, (expected.get(6) + Double.valueOf(135)));

        assertIterableEquals(actual,expected);
    }

    @Test
    void testOEE() {
    double ideal_cycle_time = 1d/100d;
    double good_count = testReport.getAcceptedProducts();
    double planned_production_time = (double)testReport.getTotalPlannedProducts()/(double)testReport.getProductionSpeed();

    double expectedOEE = (ideal_cycle_time * good_count) / planned_production_time;

    double actualOEE = testReport.getOee();

    assertEquals(expectedOEE, (double)Math.round(actualOEE * 100000d) / 100000d); //forcing 5 digit precision

    }

}