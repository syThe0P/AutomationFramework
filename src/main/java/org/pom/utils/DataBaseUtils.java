package org.pom.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
import org.pom.enums.LogLevelEnum;
import org.pom.utils.seleniumutils.WaitUtilities;

import java.util.LinkedHashMap;

public class DataBaseUtils {
    MongoClient mongo = null;
    private static DataBaseUtils dataBaseUtils;

    public static DataBaseUtils getInstance() {
        if (dataBaseUtils == null) {
            dataBaseUtils = new DataBaseUtils();
        }
        return dataBaseUtils;
    }

    public String getDataFromDatabase(String connectionString, String dbName, String collectionName, String mongoQuery, String sortingQuery) {
        return getDataFromDatabase(connectionString, dbName, collectionName, mongoQuery, sortingQuery, false);
    }

    public String getDataFromDatabase(String connectionString, String dbName, String collectionName, String mongoQuery, String sortingQuery, boolean getCount) {
        String data = "";
        String connectionString1 = "";
        if (!connectionString.contains("mongodb:")) {
            connectionString1 = getTxtRecord(connectionString);
        } else connectionString1 = connectionString;
        try {
            mongo = new MongoClient(new MongoClientURI(connectionString1));
            DB db = mongo.getDB(dbName);
            DBCollection collection = db.getCollection(collectionName);
            JsonParser jsonParser = new JsonParser();
            JsonObject mongoQueryJsonObject = (JsonObject) jsonParser.parse(mongoQuery);
            DBObject query = (DBObject) JSON.parse(mongoQueryJsonObject.toString());
            if (mongoQuery.contains("ObjectID"))
                data = collection.findOne(new ObjectId(mongoQuery.split("\\(")[1].split("\\)")[0])).toString();
            else if (getCount) data = String.valueOf(collection.count(query));
            else if (sortingQuery.isEmpty()) data = collection.findOne(query).toString();
            else {
                JsonObject mongoSortJsonObject = JsonParser.parseString(sortingQuery).getAsJsonObject();
                DBObject sortQuery = (DBObject) JSON.parse(mongoSortJsonObject.toString());
                data = collection.find(query).sort(sortQuery).one().toString();
            }
            ReportUtils.getInstance().reportStepWithoutScreenshot("Got below data from the database.", LogLevelEnum.INFO, getDbParametersForReporting(dbName, collectionName, mongoQuery, data));
        } catch (NullPointerException nullPointerException) {
            ReportUtils.getInstance().reportStepWithoutScreenshot("No data found in database <b><i>" + dbName + " -> " + collectionName + "</i></b> for query <b><i>" + mongoQuery + "</i></b>.", LogLevelEnum.INFO, getDbParametersForReporting(dbName, collectionName, mongoQuery, data));
        } catch (Exception e) {
            ReportUtils.getInstance().reportStepWithoutScreenshot("Exception <b><i>" + e.getClass().getSimpleName() + "</i></b> occurred while reading data from mongoDB.", LogLevelEnum.FAIL, getDbParametersForReporting(dbName, collectionName, mongoQuery, data));
            throw new RuntimeException(e);
        } finally {
            if (mongo != null) mongo.close();
        }
        return data;
    }

    private LinkedHashMap getDbParametersForReporting(String dbName, String collectionName, String mongoQuery, String data) {
        LinkedHashMap<String, String> linkedHashMapTestParameters = new LinkedHashMap<>();
        linkedHashMapTestParameters.put("DB Name", dbName);
        linkedHashMapTestParameters.put("Collection Name", collectionName);
        linkedHashMapTestParameters.put("Mongo Query", mongoQuery);
        linkedHashMapTestParameters.put("DB Response", data);
        return linkedHashMapTestParameters;
    }

    public String getDataFromDatabase(String connectionString, String dbName, String collectionName, String mongoQuery) {
        return getDataFromDatabase(connectionString, dbName, collectionName, mongoQuery, "");
    }

    public static String getTxtRecord(String hostName) {
        java.util.Hashtable<String, String> env = new java.util.Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

        try {
            javax.naming.directory.DirContext dirContext
                    = new javax.naming.directory.InitialDirContext(env);
            javax.naming.directory.Attributes attrs
                    = dirContext.getAttributes(hostName, new String[]{"TXT"});
            javax.naming.directory.Attribute attr
                    = attrs.get("TXT");

            String txtRecord = "";

            if (attr != null) {
                txtRecord = attr.get().toString();
            }

            return txtRecord;

        } catch (javax.naming.NamingException e) {
            ReportUtils.getInstance().reportStepWithoutScreenshot("Exception occurred --->{}" + e.getMessage(), LogLevelEnum.FAIL);
            return "";
        }
    }

    public String getDataFromDatabaseWithChiefData(String connectionString, String dbName, String collectionName, String mongoQuery, String chiefData) {
        String data = "";
        // Wait for a maximum of 15 seconds for data to be updated in the DB
        for (int i = 0; i < 15; i++) {
            data = getDataFromDatabase(connectionString, dbName, collectionName, mongoQuery);
            if (data.equals("")) {
                WaitUtilities.getInstance(null).applyStaticWait(1);
            } else if (data.contains(chiefData)) {
                break;  // Exit the loop if data contains chiefData
            } else {
                WaitUtilities.getInstance(null).applyStaticWait(1);
            }
        }
        return data;
    }
}