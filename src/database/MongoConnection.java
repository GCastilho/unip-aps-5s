package database;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.*;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.regex;

public class MongoConnection {
	private final static String dbName = "message";
	private final static int messageBatchSize = 10;
	private static MongoClient mongoClient = new MongoClient("localhost", 27017);
	private static MongoDatabase database = mongoClient.getDatabase(dbName);

	public static void addMessage(Document message,String sender, String receiver){
		//adiciona um document na collection referente a conversa sender-receiver
		//o document deve ser criado antes de enviado para essa funcao, isso ocorre em fator de flexibilidade
		if(!isGroup(receiver)) database.getCollection(alfabeticalOrder(sender, receiver)).insertOne(message);
		else database.getCollection(receiver).insertOne(message);
	}

	public static List<Document> getFirstMessageBatch (String sender, String receiver){
		//get the firs batch of messages in desc order
		//returns a list of Documents (maps
		//the content can be accessed travelling the list and using the getString(key) method
		if (!isGroup(receiver)) {
			return database.getCollection(alfabeticalOrder(sender, receiver)).
					find().limit(messageBatchSize).
					sort(new BasicDBObject("_id", -1)).into(new ArrayList <>());
		}else{
			return database.getCollection(receiver).
					find().limit(messageBatchSize).
					sort(new BasicDBObject("_id", -1)).into(new ArrayList <>());
		}
	}

	public static List<Document> getNextMessageBatch (String id, String sender, String receiver){
		//returns a list of Documents (maps
		//must receive the _id of the last message,as well the sender e receiver
		//the content can be accessed travelling the list and using the getString(key) method
		if (!isGroup(receiver)){
			return	database.getCollection(alfabeticalOrder(sender, receiver)).
				find ((lt("_id",new ObjectId(id)))).limit(messageBatchSize).
				sort(new BasicDBObject("_id",-1)).into(new ArrayList<>());
		}else{
			return database.getCollection(receiver).
				find ((lt("_id",new ObjectId(id)))).limit(messageBatchSize).
				sort(new BasicDBObject("_id",-1)).into(new ArrayList<>());
		}
	}

	public static void addChat(String user1, String user2){
		//add a new chat on the index
		// receives the users of the chat
		//MUST be called when a new chat is created or else the chat will not apear in the index
		String chatId = alfabeticalOrder(user1,user2);
		Document doc = new Document();
		doc.put("chat",chatId);
		database.getCollection("index").insertOne(doc);
	}

	public static List<Document> getUserChatList (String user){
		//pega a lista de conversas que um usuario possui
		//recebe apenas o nome do usuario
		user = "-"+user+"-";
		return database.getCollection("index").
				find(regex("chat", ".*" +Pattern.quote(user)+".*")).into(new ArrayList<>());

	}
	private static String alfabeticalOrder(String imput1, String imput2){
		//funçao auxiliar, serve para ordenar os nomes em ordem alfabetica
		String[] vector = new String[2];
		vector[0] = imput1;
		vector[1] = imput2;
		Arrays.sort(vector);
		return "-"+vector[0]+"-"+vector[1]+"-";
	}
	//funcao intermediaria, se der ruim no database todas as mensagens do usuario serao
	//enviadas para uma especie de limbo inacessivel por usuarios, sendo necessario no
	//futuro um script para limpar esse limbo
	//as chances de algo nesse nivel acontecer sao extremamente baixas porem
	// é necessario que a possivel exception de DatabaseConnection.isGroup seja tratada
		private static boolean isGroup(String receiver){
		try{
			return DatabaseConnection.isGroup(receiver);
		}catch (Exception e){
			return true;
		}

	}

}

