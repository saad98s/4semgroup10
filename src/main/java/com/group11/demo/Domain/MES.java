package com.group11.demo.Domain;

public class MES {

    OpcUaConnection opcUaConnection;    //Can we do think of any other way?

    public MES(OpcUaConnection opcUaConnection) {
        this.opcUaConnection = opcUaConnection;
    }

    public void reset() {
        opcUaConnection.write(NodeIDs.PACKML_COMMAND, 1);
        opcUaConnection.write(NodeIDs.CMD_CHANGE_REQUEST, true);
    }

    public void initializeProduction(String batchID, String productID, String amountOfProducts, String machineSpeed) {
        opcUaConnection.write(NodeIDs.BATCH_ID_NEXT_BATCH, Float.valueOf(batchID));
        opcUaConnection.write(NodeIDs.PRODUCT_ID_NEXT_BATCH, Float.valueOf(productID));
        opcUaConnection.write(NodeIDs.AMOUNT_OF_PRODUCTS_NEXT_BATCH, Float.valueOf(amountOfProducts));
        opcUaConnection.write(NodeIDs.MACH_SPEED_WRITE, Float.valueOf(machineSpeed));
    }

    public void startProduction() {
        opcUaConnection.write(NodeIDs.PACKML_COMMAND, 2);
        opcUaConnection.write(NodeIDs.CMD_CHANGE_REQUEST, true);
    }

    public void stopProduction() {
        opcUaConnection.write(NodeIDs.PACKML_COMMAND, 3);
        opcUaConnection.write(NodeIDs.CMD_CHANGE_REQUEST, true);
    }

    public void abort() {
        opcUaConnection.write(NodeIDs.PACKML_COMMAND, 4);
        opcUaConnection.write(NodeIDs.CMD_CHANGE_REQUEST, true);
    }

    public void clear() {
        opcUaConnection.write(NodeIDs.PACKML_COMMAND, 5);
        opcUaConnection.write(NodeIDs.CMD_CHANGE_REQUEST, true);
    }

}

