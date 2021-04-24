package com.group11.demo.Domain;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;

import javax.websocket.Endpoint;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import static org.eclipse.milo.opcua.stack.core.util.EndpointUtil.updateUrl;

public class OpcUaConnection {
    OpcUaClient client;
    final int NAMESPACEINDEX = 6;

    private static final AtomicLong clientHandles = new AtomicLong(1L);

    public OpcUaConnection(String type) {

        try {
            if (type.toLowerCase()=="sim"){
                List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://127.0.0.1:4840").get(); //Simulator
                OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
                cfg.setEndpoint(endpoints.get(0));
                this.client = OpcUaClient.create(cfg.build());
                client.connect().get();
                System.out.println("Connecting to simulator");
            } else if (type.toLowerCase()=="phys"){
                List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://192.168.0.122:4840").get();
                EndpointDescription test = EndpointUtil.updateUrl(endpoints.get(0), "192.168.0.122", 4840);
                OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
                cfg.setEndpoint(endpoints.get(0));
                cfg.setEndpoint(test);
                this.client = OpcUaClient.create(cfg.build());
                client.connect().get();
                System.out.println("Connecting to physical machine");
            } else {
                System.out.println("No connection type specified (sim/phys)");
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public String read(NodeIDs node) {

        NodeId nodeID = new NodeId(NAMESPACEINDEX, String.valueOf(node));

        DataValue dataValue = null;
        try {
            dataValue = client.readValue(0, TimestampsToReturn.Both, nodeID).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return dataValue.getValue().getValue().toString();
    }

    public void write(NodeIDs node, Object value) {

        NodeId nodeID = new NodeId(NAMESPACEINDEX, String.valueOf(node));

        try {
            client.writeValue(nodeID, DataValue.valueOnly(new Variant(value))).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(NodeIDs node) {

        NodeId nodeID = new NodeId(NAMESPACEINDEX, String.valueOf(node));

        try {
            // what to read
            ReadValueId readValueId = new ReadValueId(nodeID, AttributeId.Value.uid(), null, null);

            // important: client handle must be unique per item
            UInteger clientHandle = Unsigned.uint(clientHandles.getAndIncrement());
            //int clientHandle = 123456789;
            MonitoringParameters parameters = new MonitoringParameters(
                    clientHandle,
                    1000.0,     // sampling interval
                    null,       // filter, null means use default
                    Unsigned.uint(10),   // queue size
                    true        // discard oldest
            );

            // creation request
            MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);


            // setting the consumer after the subscription creation
            BiConsumer<UaMonitoredItem, Integer> onItemCreated = (item, id) -> item.setValueConsumer(OpcUaConnection::onSubscriptionValue);

            // create a subscription @ 1000ms
            UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();

            List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, Arrays.asList(request), onItemCreated).get();

            for (UaMonitoredItem item : items) {
                if (item.getStatusCode().isGood()) {
                    System.out.println("item created for nodeId=" + item.getReadValueId().getNodeId());
                } else {
                    System.out.println("failed to create item for nodeId=" + item.getReadValueId().getNodeId() + " (status=" + item.getStatusCode() + ")");
                }
            }

            // let the example run for 15 seconds then terminate
            Thread.sleep(15000);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    private static void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
        System.out.println("subscription value received: item=" + item.getReadValueId().getNodeId() + ", value=" + value.getValue());
    }
}


