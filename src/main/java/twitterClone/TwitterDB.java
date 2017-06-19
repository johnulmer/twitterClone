package twitterClone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TwitterDB {
	
	public static final String DBURL = "jdbc:sqlite:twitterClone.db";
	
    private static Connection connect() {
        // SQLite connection string
        String url = TwitterDB.DBURL;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
	
	public static ArrayList<User> UnfollowedUsers(int userID) {
		ArrayList<User> unfollowedUsers = new ArrayList<User>();
		String sqlString = "Select * FROM Users WHERE UserID NOT IN " + 
				"(SELECT UserID FROM Users, Followers WHERE Followers.FollowedByUserID = " + userID + 
				" AND Users.UserID = Followers.FollowedUserID)";
		System.out.println(sqlString);
        try (Connection conn = connect();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(sqlString)){
               while (rs.next()) {
            	   System.out.println("adding user");
            	   User u = new User(rs.getString("UserName"), "", rs.getString("handle"), rs.getInt("UserID"));
            	   unfollowedUsers.add(u);
               }
           } catch (SQLException e) {
               System.out.println(e.getMessage());
           }
		return unfollowedUsers;
	}

// Read user from DB by userID
public static User GetUserByUserID(int userID) {
	User u = new User();
	String sqlString = "SELECT UserID, UserName, Password, Handle FROM Users WHERE UserID = " + userID;
    try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlString)){
           
           // loop through the result set
           while (rs.next()) {
               System.out.println(rs.getInt("UserID") +  "\t" + 
                                  rs.getString("UserName") + "\t" +
                                  rs.getString("Handle"));
       			u = new User(rs.getString("UserName"), rs.getString("Password"), rs.getString("Handle"));
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	System.out.println("reading user");
	return u;
}

public static String GetUserHandle(User u) {
	return "should be a handle";
}

// Authenticate user based on userName and password
public static int Authenticate(String userName, String password) {
	int userID = -1;
	String sqlString = "SELECT UserID FROM Users WHERE UserName = '" + userName + "' AND Password = '" + password + "'";
    try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlString)){
           while (rs.next()) {
        	   userID = rs.getInt("UserID");
           }
           System.out.println("found ID: " + userID);
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	return userID;
}

public static int ProcessRegistration(String userName, String password, String handle) {
	String sqlString = "INSERT INTO Users (UserName, Password, Handle) VALUES " +
			"('" + userName + "',  '" + password + "', '" + handle + "')";
	System.out.println(sqlString);
    try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlString)){
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
    return GetUserByUserName(userName);
}
// Read user from DB by userName
public static int GetUserByUserName(String userName) {
	int userID = -1;
	String sqlString = "SELECT UserID FROM Users WHERE UserName = '" + userName + "'";
	System.out.println(sqlString);
    try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlString)){
           while (rs.next()) {
        	   userID = rs.getInt("UserID");
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	return userID;
}
// Read user from DB by userName
public static int GetUserByHandle(String handle) {
	int userID = -1;
	String sqlString = "SELECT UserID FROM Users WHERE Handle = '" + handle + "'";
	System.out.println(sqlString);
    try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlString)){
           while (rs.next()) {
        	   userID = rs.getInt("UserID");
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	return userID;
}
public static ArrayList<User> FollowedUsers(int userID) {
	ArrayList<User> followedUsers = new ArrayList<User>();
	String sqlString = "Select * FROM Users, Followers WHERE Followers.FollowedByUserID = " + 
			userID + " AND Users.UserID = Followers.FollowedUserID AND Users.UserID != " + userID;
	System.out.println(sqlString);
    try (Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlString)){
           while (rs.next()) {
        	   System.out.println("adding user");
        	   User u = new User(rs.getString("UserName"), "", rs.getString("handle"),rs.getInt("UserID"));
        	   followedUsers.add(u);
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	return followedUsers;
}
}