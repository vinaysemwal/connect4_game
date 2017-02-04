package com.gluck.gaming.db.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.client.MongoCollection;

/**
 * Test class for testing CRUD operations to Mongo DB using {@link MongoDbConnectionManager}
 *
 * @author Vinay Semwal
 */
public class MongoDbConnectionManagerTest {

    private static MongoDbConnectionManager connectionManager;

    /**
     * Create {@link MongoDbConnectionManager} object to be used in tests using mongoDB's default "test" database.
     */
    @BeforeClass
    public static void setUpConnection() {
        connectionManager = new MongoDbConnectionManager("test", "localhost", 27017);

    }

    /**
     * Test failure scenarios for {@link MongoDbConnectionManager} object construction.
     */
    @Test
    public void testConnectionManagerCreationFailure() {
        try {
            new MongoDbConnectionManager("test", null, 1234);
        } catch (final NullPointerException e) {
            assertEquals("DB host cannot be null.", e.getMessage());
        }
        try {
            new MongoDbConnectionManager("test", "localhost", null);
        } catch (final NullPointerException e) {
            assertEquals("DB port cannot be null.", e.getMessage());
        }
        try {
            new MongoDbConnectionManager(null, "localhost", 1234);
        } catch (final NullPointerException e) {
            assertEquals("Database name cannot be null.", e.getMessage());
        }
    }

    /**
     * Test that collection gets created on createCollection API call
     */
    @Test
    public void testCreateCollection() {
        MongoCollection<Document> collection = null;
        try {
            connectionManager.createCollection("testGame");
            collection = connectionManager.getCollection("testGame");
            assertNotNull(collection);
        } finally {
            collection.drop();

        }

    }

    /**
     * Test that getCollection doesn't create a collection unless a document has been added to it after getCollection call.
     */
    @Test
    public void testNonExistentCollection() {
        connectionManager.getCollection("TestGame");
        assertFalse("Expected testGame to not present in the list", connectionManager.listCollectionsOfDatabase().contains("testGame"));
    }

    /**
     * Test to create document for a given collection
     */
    @Test
    public void testCreateDocument() {
        MongoCollection<Document> collection = null;
        try {
            connectionManager.createCollection("testGame");
            collection = connectionManager.getCollection("testGame");
            assertNotNull(collection);
            final Document doc = new Document("name", "MongoDB").append("type", "database").append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6")).append("info", new Document("x", 203).append("y", 102));
            final String documentId = connectionManager.createDocument("testGame", doc);
            assertNotNull(connectionManager.findDocument("testGame", doc));
            assertEquals(documentId, connectionManager.findDocument("testGame", doc).get("_id").toString());
        } finally {
            collection.drop();

        }
    }

    /**
     * Test to search a document using its unique identifier.
     */
    @Test
    public void testFindDocumentById() {
        MongoCollection<Document> collection = null;
        try {
            connectionManager.createCollection("testGame");
            collection = connectionManager.getCollection("testGame");
            assertNotNull(collection);
            final Document doc = new Document("name", "MongoDB").append("type", "database").append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6")).append("info", new Document("x", 203).append("y", 102));
            collection.insertOne(doc);
            final Document fetchedDocument = connectionManager.findDocument("testGame", doc);
            assertNotNull(fetchedDocument);
            final ObjectId id = fetchedDocument.getObjectId("_id");
            assertTrue(connectionManager.findDocumentById("testGame", id.toString()).isPresent());
            final Map<String, String> queryMap = new HashMap<String, String>();
            //Query a non-existent document, API should return an empty optional.
            queryMap.put("name", "MongoDB");
            queryMap.put("type", "database123");
            final Optional<Document> testDoc = connectionManager.findDocumentUsingFilters("testGame", queryMap);
            assertFalse(testDoc.isPresent());
        } finally {
            collection.drop();

        }
    }

    /**
     * Test to update a document in mongo DB
     */
    @Test
    public void testUpdateDocument() {
        MongoCollection<Document> collection = null;
        try {
            connectionManager.createCollection("testGame");
            collection = connectionManager.getCollection("testGame");
            assertNotNull(collection);
            final Document doc = new Document("name", "MongoDB").append("type", "database").append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6")).append("info", new Document("x", 203).append("y", 102));
            collection.insertOne(doc);
            final Document fetchedDocument = connectionManager.findDocument("testGame", doc);
            assertNotNull(fetchedDocument);
            final ObjectId id = fetchedDocument.getObjectId("_id");
            connectionManager.updateDocument(id.toString(), new Document("name", "updateMongoDBTest").append("count", 2), "testGame");
            final Document updatedDocument = connectionManager.findDocumentById("testGame", id.toString()).get();
            assertTrue(updatedDocument.getString("name").equals("updateMongoDBTest"));
            assertTrue(updatedDocument.getInteger("count") == 2);
            assertTrue(updatedDocument.getString("type").equals("database"));
        } finally {
            collection.drop();

        }
    }

}
