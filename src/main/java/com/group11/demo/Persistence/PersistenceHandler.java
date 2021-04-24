package com.group11.demo.Persistence;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.group11.demo.Domain.*;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class PersistenceHandler implements IPersistenceHandler {
    private static final String URL = "localhost";
    private static final int PORT = 27017;
    private static final String DATABASE_NAME = "SemProject";
    private static PersistenceHandler instance;
    private MongoDatabase database;

    private PersistenceHandler() {
        initializeMongoDatabase();
    }

    public static PersistenceHandler getInstance() {
        if (instance == null) {
            instance = new PersistenceHandler();
        }
        return instance;
    }

    private void initializeMongoDatabase() {
        try {
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            MongoClientSettings settings = MongoClientSettings.builder()
                    .codecRegistry(pojoCodecRegistry)
                    .applyConnectionString(new ConnectionString("mongodb://" + URL + ":" + PORT))
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DATABASE_NAME);
        } finally {
            if (database == null){
                System.out.println("Database = null");
                System.exit(-1);
            }
        }
    }


    public List<BatchReport> getBatchReports() {
        MongoCollection<BatchReport> mongoCollection = database.getCollection("batchReport", BatchReport.class);
        return mongoCollection.find().into(new ArrayList<>());
    }

    @Override
    public BatchReport findByID(float id) {
        MongoCollection<BatchReport> mongoCollection = database.getCollection("batchReport", BatchReport.class);
        return mongoCollection.find(Filters.eq("_id",id)).first();
    }

    @Override
    public List<BatchReport> findByProductType(float productType) {
        MongoCollection<BatchReport> mongoCollection = database.getCollection("batchReport", BatchReport.class);
        return mongoCollection.find(Filters.eq("productType",productType)).into(new ArrayList<>());
    }

    @Override
    public void addReport(BatchReport batchReport) {
        MongoCollection<BatchReport> mongoCollection = database.getCollection("batchReport", BatchReport.class);

        float incomingBatchID = batchReport.getBatchID();
        if (this.findByID(incomingBatchID) != null) {
            mongoCollection.deleteOne(Filters.eq("_id", incomingBatchID));
        }
        mongoCollection.insertOne(batchReport);

    }

    @Override
    public void addReports(List<BatchReport> batchReportList) {
        MongoCollection<BatchReport> mongoCollection = database.getCollection("batchReport", BatchReport.class);
        mongoCollection.insertMany(batchReportList);
    }

    @Override
    public void removeReportByBatchID(float id) {
        MongoCollection<BatchReport> mongoCollection = database.getCollection("batchReport", BatchReport.class);
        mongoCollection.deleteOne(Filters.eq("_id",id));
    }

}
