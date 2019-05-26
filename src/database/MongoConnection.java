package database;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.*;

import org.bson.Document;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.regex;

public class MongoConnection {
	private final static String dbName = "message";
	private final static int messageBatchSize = 10;
	private static MongoClient mongoClient = new MongoClient("localhost", 27017);
	private static MongoDatabase database = mongoClient.getDatabase(dbName);

	public static void addMessage(Document message,String sender, String receiver){
		//
		database.getCollection(alfabeticalOrder(sender,receiver)).insertOne(message);
	}

	public static List<Document> getChatMessageList (String sender, String receiver){
		//the -1 on sort means that the get order is desc
		return	database.getCollection(alfabeticalOrder(sender,receiver)).
				find().
				sort(new BasicDBObject("_id",-1)).into(new ArrayList<>());
	}

	public static List<Document> getMessageList (String sender, String receiver){
		//the -1 on sort means that the get order is desc
		return	database.getCollection(alfabeticalOrder(sender,receiver)).
				find().limit(messageBatchSize).
				sort(new BasicDBObject("_id",-1)).into(new ArrayList<>());
	}

	public static List<Document> getNextMessageList (Document doc){
		//the -1 on sort means that the get order is desc
		return	database.getCollection(alfabeticalOrder(doc.getString("sender"),doc.getString("receiver"))).
				find ((gt("_id",doc.getObjectId("_id")))).limit(messageBatchSize).
				sort(new BasicDBObject("_id",-1)).into(new ArrayList<>());
	}

	public static void addChat(String user1, String user2){

		String chatId = alfabeticalOrder(user1,user2);
		Document doc = new Document();
		doc.put("chat",chatId);
		database.getCollection("index").insertOne(doc);
	}

	public static List<Document> getUserChatList (String user){
		return database.getCollection("index").
				find(regex("chat", ".*" +Pattern.quote(user)+".*")).into(new ArrayList<>());

	}

	private static String alfabeticalOrder(String imput1, String imput2){
		String[] vector = new String[2];
		vector[0] = imput1;
		vector[1] = imput2;
		Arrays.sort(vector);
		return ""+vector[0]+"-"+vector[1]+"";
	}

}

