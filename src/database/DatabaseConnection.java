package database;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class DatabaseConnection {
	private static Connection conn;

	private static Connection getConnection() throws SQLException,ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");

		String serverName = "127.0.0.1:3306";        //caminho do servidor do BD
		String mydatabase = "unip_aps_5s";            //nome do seu banco de dados
		String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?useTimezone=true&serverTimezone=UTC";
		String username = "aps";					//nome de um usuário de seu BD
		String password = "";						//sua senha de acesso
		return conn = DriverManager.getConnection(url, username, password);
	}


	public static boolean validCredentials(String username, String password) throws Exception {
		password = getSHA512(password);
		try {
			PreparedStatement stmt = getConnection().prepareStatement(
					"select id from credential where username = ? and password_hash = ?");
			stmt.setString(1, username);
			stmt.setString(2, password);

			//the prepared statement returns a result set
			//the Result Set of the return executes next() to verify if the first row of the select is empty
			return stmt.executeQuery().next();
		} finally {
			conn.close();
		}
	}

	public static String makeCookie(String user, String password) throws Exception {
		long time = new Date().getTime();
		try {
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"insert into cookie (username,sessionID,timestamp) values (?,?,?)"
			);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, password = getSHA512(password + user + time));
			preparedStatement.setLong(3, time);

			preparedStatement.execute();

			return password;
		} finally {
			conn.close();
		}
	}

	public static void updateCookie(String sessionId) throws Exception {
		try {
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"update cookie set timestamp = ? where sessionID = ?"
			);
			preparedStatement.setLong(1, new Date().getTime());
			preparedStatement.setString(2, sessionId);

			preparedStatement.execute();
		} finally {
			conn.close();
		}
	}

	public static boolean validCookie(String sessionId) throws Exception {
		try {
			ResultSet resultSet = getConnection().createStatement().executeQuery(
					"select timestamp from cookie where sessionId = '"+sessionId+"'"
			);

			//verify if cookie timestamp is bigger than 10 minutes (in milliseconds)
			return resultSet.next() && resultSet.getLong(1) > new Date().getTime()-600000;
		} finally {
			conn.close();
		}
	}

	public static String getUser(String sessionId) throws Exception {
		try {
			ResultSet resultSet = getConnection().createStatement().executeQuery(
					"select username from cookie where sessionId = '" + sessionId + "'"
			);
			if (resultSet.next()) {
				return resultSet.getString(1);
			} else {
				throw new Exception("Username not found for given sessionID");
			}
		} finally {
			conn.close ();
		}
	}

	public static List<String> getUserList() throws Exception {
		try {
			List<String> list = new ArrayList<>();
			ResultSet rs = getConnection().createStatement().executeQuery(
					"select username from credential"
			);
			while (rs.next()) {
				String user = rs.getString(1);
				if (!user.equals("root")) list.add(user);
			}

			//the List can later be cast into other List
			return list;
		} finally {
			conn.close();
		}
	}
	//retorna todos os grupos em que o usuario esta incluso
	public static List<String> getUserGroupList(String groupMember) throws Exception {
		try {
			List<String> list = new ArrayList<>();
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"select groupName, groupID from chatGroup where groupMember = ?"
			);
			preparedStatement.setString(1, groupMember);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()){
				list.add(rs.getString(1)+"-"+rs.getString(2));
			}

			return list;
		} finally {
			conn.close();
		}
	}

	public static void dropCookies() throws Exception {
		try {
			getConnection().createStatement().execute("truncate cookie");
		} finally {
			conn.close();
		}
	}

	public static void dropCookie(String sessionID) throws Exception {
		try {
			getConnection().createStatement().execute(
					"delete from cookie where sessionID = '" + sessionID + "'"
			);
		} finally {
			conn.close();
		}
	}
	//cria um grupo e adiciona todos os usuarios que foram enviados
	public static void createGroup(String groupName, String[] userNames) throws Exception {
		try {
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"insert into chatGroup (groupName, groupMember, groupID) values (?,?,?)"
			);
			preparedStatement.setString(1, groupName);
			preparedStatement.setString(3, getSHA1(groupName+new Date().getTime()));
			for(String user : userNames){
				preparedStatement.setString(2, user);
				preparedStatement.execute();
			}
		} finally {
			conn.close();
		}
	}
	//adiciona um usuario ao grupo
	public static void addGroupUser(String groupName, String userName) throws Exception {
		try {
			String groupID = groupName.substring(groupName.length()-40);
			groupName = groupName.replace("-"+groupID,"");
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"insert into chatGroup (groupName, groupMember, groupID) values (?,?,?)"
			);
			preparedStatement.setString(1, groupName);
			preparedStatement.setString(2, userName);
			preparedStatement.setString(3, groupID);
			preparedStatement.execute();
		} finally {
			conn.close();
		}
	}
	//remove usuario do grupo
	public static void removeGroupUser(String groupName, String userName) throws Exception {
		try {
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"delete from chatGroup where groupName = ? and groupMember = ?"
			);
			preparedStatement.setString(1, groupName);
			preparedStatement.setString(2, userName);
			preparedStatement.execute();
		} finally {
			conn.close();
		}
	}
	//retorna todos os usuarios do grupo
	public static List<String> getGroupUserList(String groupName) throws Exception {
		try {
			List<String> list = new ArrayList<>();
			String groupID = groupName.substring(groupName.length()-40);
			groupName = groupName.replace("-"+groupID,"");
			PreparedStatement preparedStatement = getConnection().prepareStatement(
					"select groupMember from chatGroup where groupName = ? and groupID = ?"
			);
			preparedStatement.setString(1, groupName);
			preparedStatement.setString(2, groupID);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()){
				list.add(rs.getString(1));
			}
			return list;
		} finally {
			conn.close();
		}
	}

	static boolean isGroup (String input) throws Exception {
		try {
			if(input.length()<41){
				return false;
			}
			input = input.substring(input.length()-41);
			Pattern pattern = Pattern.compile("-\\w{40}");
			Scanner scanner = new Scanner( input );
			return scanner.hasNext(pattern);
		} finally {
			conn.close();
		}
	}

	private static String getSHA512(String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(input.getBytes(StandardCharsets.UTF_8));

		return (String.format("%0128x", new BigInteger(1, digest.digest())));
	}
	private static String getSHA1(String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(input.getBytes(StandardCharsets.UTF_8));
		input = (String.format("%040x", new BigInteger(1, digest.digest())));
		return input;
	}

}
