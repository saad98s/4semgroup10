package com.group11.demo.Domain;

import java.util.List;

public interface IPersistenceHandler {

    List<BatchReport> getBatchReports();

    BatchReport findByID(float id);

    List<BatchReport> findByProductType(float productType);

    void addReport(BatchReport batchReport);

    void addReports(List<BatchReport> batchReportList);

    void removeReportByBatchID(float id);

}