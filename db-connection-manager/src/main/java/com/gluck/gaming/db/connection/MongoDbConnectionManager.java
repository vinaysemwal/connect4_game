package com.gluck.gaming.db.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Class to manage DB calls to be made to a specific database defined in MongoDB. All the connections are managed by a mongo db client instance contained in
 * this class that takes care of connection pool management internally.
 *
 * @author Vinay Semwal
 */
public class MongoDbConnectionManager {

    private static final String DOCUMENT_ID = "_id";

    private final MongoClient client;

    private final String dbHost;

    private final Integer dbPort;

    private final String databaseName;

    private final MongoDatabase database;

    /**
     * @param databaseName mongo DB database name to which the connections will be managed.
     * @param dbHost database host name
     * @param dbPort database port
     */
    public MongoDbConnectionManager(final String databaseName, final String dbHost, final Integer dbPort) {
        super();
        this.dbHost = Objects.requireNonNull(dbHost, "DB host cannot be null.");
        this.dbPort = Objects.requireNonNull(dbPort, "DB port cannot be null.");
        this.databaseName = Objects.requireNonNull(databaseName, "Database name cannot be null.");
        client = new MongoClient(dbHost, dbPort);
        database = client.getDatabase(this.databaseName);

    }

    /**
     * API to create a collection
     *
     * @param collectionName name of the collection to create.
     */
    public void createCollection(final String collectionName) {
        database.createCollection(collectionName);
    }

    /**
     * @param collectionName collection to retrieve
     * @return {@link MongoCollection}
     */
    public MongoCollection<Document> getCollection(final String collectionName) {
        final MongoCollection<Document> collection = database.getCollection(collectionName);
        if (Objects.isNull(collection)) {
            //TODO: throw exception
        }
        return collection;
    }

    /**
     * @param collectionName name of the collection for which the document is to be created
     * @param document {@link Document} to create.
     * @return unique identifier of the game created.
     */
    public String createDocument(final String collectionName, final Document document) {
        final MongoCollection<Document> collection = getCollection(collectionName);
        collection.insertOne(document);
        return collection.find(document).first().getObjectId(DOCUMENT_ID).toString();
    }

    /**
     * API to find the first occurrence of the document in a collection.
     *
     * @param collectionName name of the collection from which the document is to be fetched.
     * @param document {@link Document} to fetch
     * @return fetched document
     */
    public Document findDocument(final String collectionName, final Document document) {
        return getCollection(collectionName).find(document).first();
    }

    /**
     * @param collectionName Name of the collection to be queried to find the document.
     * @param id unique identifier of the document to be fetched.
     * @return Document matching id provided else an empty Optional
     */
    public Optional<Document> findDocumentById(final String collectionName, final String id) {
        final BasicDBObject query = new BasicDBObject(DOCUMENT_ID, new ObjectId(id));
        return Optional.ofNullable(getCollection(collectionName).find(query).first());
    }

    /**
     * @param collectionName Name of the collection to which the document belongs.
     * @param filter map of document field names and field values to be used to query the collection.
     * @return first Document matching the filters provided else an empty Optional
     */
    public Optional<Document> findDocumentUsingFilters(final String collectionName, final Map<String, String> filter) {
        final BasicDBObject query = new BasicDBObject(filter);
        final MongoCollection<Document> collection = getCollection(collectionName);
        filter.entrySet();
        return Optional.ofNullable(collection.find().filter(query).first());
    }

    /**
     * @param documentId unique identifier of the document to update
     * @param updatedValues values to be updated present
     * @param collectionName collection name to which the document belongs.
     */
    public void updateDocument(final String documentId, final Document updatedValues, final String collectionName) {
        final BasicDBObject query = new BasicDBObject(DOCUMENT_ID, new ObjectId(documentId));
        getCollection(collectionName).updateOne(query, new Document("$set", updatedValues));
    }

    /**
     * API to delete a document using document Id.
     *
     * @param documentId id of the document to delete
     * @param collectionName name of the collection containing document to delete.
     */
    public void deleteDocument(final String documentId, final String collectionName) {
        final BasicDBObject deleteQuery = new BasicDBObject(DOCUMENT_ID, new ObjectId(documentId));
        getCollection(collectionName).deleteOne(deleteQuery);
    }

    /**
     * @param collectionName name of the collection to drop
     */
    public void deleteCollection(final String collectionName) {
        final MongoCollection<Document> collection = getCollection(collectionName);
        collection.drop();
    }

    /**
     * API to list names of all the collections of database.
     *
     * @return list of the collection names
     */
    public List<String> listCollectionsOfDatabase() {
        final List<String> collectionNames = new ArrayList<String>();
        final Iterator<String> iterator = database.listCollectionNames().iterator();
        while (iterator.hasNext()) {
            collectionNames.add(iterator.next());
        }
        return collectionNames;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @return the client
     */
    public MongoClient getClient() {
        return client;
    }

    /**
     * @return the dbHost
     */
    public String getDbHost() {
        return dbHost;
    }

    /**
     * @return the dbPort
     */
    public Integer getDbPort() {
        return dbPort;
    }

}
