package com.group11.demo.Domain;

public enum NodeIDs {
    PRODUCTS_PROCESSED("::Program:Cube.Admin.ProdProcessedCount"),
    PRODUCTS_DEFECTIVE("::Program:Cube.Admin.ProdDefectiveCount"),
    STOP_REASON_VALUE("::Program:Cube.Admin.StopReason.ID"),
    PRODUCT_ID("::Program:Cube.Admin.Parameter[0].Value"),
    STATE("::Program:Cube.Status.StateCurrent"),
    MACH_SPEED_U("::Program:Cube.Status.MachSpeed"),
    MACH_SPEED_P("::Program:Cube.Status.CurMachSpeed"),
    CURRENT_BATCH_ID("::Program:Cube.Status.Parameter[0].Value"),
    AMOUNT_OF_PRODUCTS_IN_BATCH("::Program:Cube.Status.Parameter[1].Value"),
    RELATIVE_HUMIDITY("::Program:Cube.Status.Parameter[2].Value"),
    TEMP("::Program:Cube.Status.Parameter[3].Value"),
    VIBRATION("::Program:Cube.Status.Parameter[4].Value"),
    MACH_SPEED_WRITE("::Program:Cube.Command.MachSpeed"),
    PACKML_COMMAND("::Program:Cube.Command.CntrlCmd"),
    CMD_CHANGE_REQUEST("::Program:Cube.Command.CmdChangeRequest"),
    BATCH_ID_NEXT_BATCH("::Program:Cube.Command.Parameter[0].Value"),
    PRODUCT_ID_NEXT_BATCH("::Program:Cube.Command.Parameter[1].Value"),
    AMOUNT_OF_PRODUCTS_NEXT_BATCH("::Program:Cube.Command.Parameter[2].Value"),
    FILLING_INV("::Program:FillingInventory"),
    INV_BARLEY("::Program:Inventory.Barley"),
    INV_MALT("::Program:Inventory.Malt"),
    INV_HOPS("::Program:Inventory.Hops"),
    INV_WHEAT("::Program:Inventory.Wheat"),
    INV_YEAST("::Program:Inventory.Yeast"),
    MAINTENANCE_COUNTER("::Program:Maintenance.Counter"),
    MAINTENANCE_TRIGGER("::Program:Maintenance.Trigger"),
    MAINTENANCE_STATE("::Program:Maintenance.State");

    private final String s;

    NodeIDs(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
