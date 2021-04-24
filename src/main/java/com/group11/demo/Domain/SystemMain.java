package com.group11.demo.Domain;

public class SystemMain {

    public SCADA scada;
    public MES mes;

    public SystemMain(OpcUaConnection opcUaConnection) {
        this.scada = new SCADA(opcUaConnection);
        this.mes = new MES(opcUaConnection);
    }

    public SCADA getScada() {
        return scada;
    }

    public MES getMes() {
        return mes;
    }

    public BatchReport generateBatchReport() {
        return new BatchReport(scada.batchID, scada.productType, scada.productionSpeed, scada.totalProducts, scada.productsProduced, scada.defectiveProducts, scada.temperature, scada.humidity, scada.errorLog, scada.timeSpentInStates);
    }

}
