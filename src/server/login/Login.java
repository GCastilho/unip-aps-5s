package login;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.sql.Timestamp;
import java.util.Date;
public class Login {

	public static void main(String[] args) {
		if (validCredentiais("gabriel","FA585D89C851DD338A70DCF535AA2A92FEE7836DD6AFF1226583E88E0996293F16BC009C652826E0FC5C706695A03CDDCE372F139EFF4D13959DA6F1F5D3EABE")){
			System.out.println("logado");
		}else{
			System.out.print("erro");
		}

	}

	public static boolean validCredentiais(String user, String password){
		password = getSHA512(password);
		String query = "select count(id) from login_data where name = '"+user+"' and password_enc ='"+password+"'";
		Connection con = DatabaseConnection.getConexaoMySQL();
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			int count = 0;
			while (rs.next()){
			count = rs.getInt("count(id)");
			}
			if (count == 1){
				return true;
			}else {
				return false;
			}
		}catch(SQLException e){ e.printStackTrace();}
		return false;
	}
	
	public static String addCookie(String user,String password){
		Connection con = DatabaseConnection.getConexaoMySQL();

		Date date= new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		password =  getSHA512(password+user+ts);

		String query = " insert into cookie (user_name,sessionID,timeStamp)"
		+ " values (?,?,?)";
		try{
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1,user );
			preparedStmt.setString (2,password);
			preparedStmt.setString (3,""+ts+"");

			preparedStmt.execute();}catch(SQLException e){ e.printStackTrace();
		}
		System.out.println("sessao adicionada com sucesso ao banco de dados");
		return password;
	}

	public static String getSHA512(String input){
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(input.getBytes("utf8"));
			return (String.format("%0128x", new BigInteger(1, digest.digest())));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return "";
	}
	
}
