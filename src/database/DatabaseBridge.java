package database;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;

public class DatabaseBridge {
	public static boolean validCredentials(String username, String password) throws Exception {
		password = getSHA512(password);
		String query = "select id from credential where username = '"+username+"' and password_hash = '"+password+"'";

		//uses the statement object that returned from createStatement() to use the executequery() method
		//the resultset returned execute next() to verify if the first row of the select is empty
		return DatabaseConnection.executeQuery(query).next();
	}

	public static boolean validCookie(String sessionId) throws Exception {
		String query = "select timestamp from cookie where sessionId = '"+sessionId+"'";

		ResultSet resultSet = DatabaseConnection.executeQuery(query);

		//verify if cookie timestamp is bigger than 10 minutes (in miliseconds)
		return resultSet.next() && resultSet.getLong(1) > new Date().getTime()-600000;
	}

	public static String updateCookie(String username ,String sessionId) throws Exception {
		String query = "delete from cookie where sessionId ='"+sessionId+"'";

		// TODO: Essa função não funciona, tá? Não mexi nela mas ela não funciona, só dá uma olhada nesse código
		DatabaseConnection.executeQuery(query);

		return makeCookie(username, sessionId);
	}

	public static String makeCookie(String user, String password) throws Exception {
		long time = new Date().getTime();

		password =  getSHA512(password+user+time);

		String query = "insert into cookie (username,sessionID,timestamp)"
		+ " values (?,?,?)";

		PreparedStatement preparedStatement = DatabaseConnection.getPreparedStatment(query);
		preparedStatement.setString(1,user );
		preparedStatement.setString(2,password);
		preparedStatement.setLong(3,time);

		preparedStatement.execute();

		return password;
	}

	private static String getSHA512(String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(input.getBytes(StandardCharsets.UTF_8));

		return (String.format("%0128x", new BigInteger(1, digest.digest())));
	}
	
}
