package database;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnection {
	private final static String dbName = "app";
	private final static String tableName = "message";

	private static MongoClient mongoClient = new MongoClient("localhost", 27017);
	private static MongoDatabase database = mongoClient.getDatabase(dbName);

	public static void addMessage(Document message){
		database.getCollection(tableName).insertOne(message);
	}
}

