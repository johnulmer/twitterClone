package twitterClone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TwitterDB {
	
	public static final String DBURL = "jdbc:sqlite:twitterClone.db";
	
    private static Connection connect() {
        String url = TwitterDB.DBURL;     // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

// Read user from DB by userID
	public static User getUserByUserID(int userID) {
		User u = new User();
		Connection conn = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			conn = connect();
			prepStmt = conn.prepareStatement("SELECT UserID, UserName, Password, Handle FROM Users WHERE UserID = ?");
			prepStmt.setInt(1, userID);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				u = new User(rs.getString("UserName"), rs.getString("Password"), rs.getString("Handle"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				rs.close();
				prepStmt.close();
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println("reading user by ID");
		return u;
	}

public static String getUserHandle(User u) {
	return "should be a handle";
}

// Authenticate user based on userName and password
	public static int authenticate(String userName, String password) {
		int userID = -1;
		Connection conn = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			conn = connect();
			prepStmt = conn.prepareStatement("SELECT UserID FROM Users WHERE UserName = ? AND Password = ?");
			prepStmt.setString(1, userName);
			prepStmt.setString(2, password);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				userID = rs.getInt("UserID");
			}
			System.out.println("found ID: " + userID);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				rs.close();
				prepStmt.close();
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println("reading user by ID");
		return userID;
	}

// add a new user, return newly added user	
public static int processRegistration(String userName, String password, String handle) {
	Connection conn = null;
	PreparedStatement prepStmt = null;
	try {
		conn = connect();
		prepStmt = conn.prepareStatement("INSERT INTO Users (UserName, Password, Handle) VALUES (?, ?, ?)");
		prepStmt.setString(1, userName);
		prepStmt.setString(2, password);
		prepStmt.setString(2, handle);
		prepStmt.executeUpdate();
	
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       } finally {
			try {
				prepStmt.close();
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
       }
    return getUserByUserName(userName);
}
// Read user from DB by userName
	public static int getUserByUserName(String userName) {
		int userID = -1;
		Connection conn = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			conn = connect();
			prepStmt = conn.prepareStatement("SELECT UserID FROM Users WHERE UserName = ?");
			prepStmt.setString(1, userName);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				userID = rs.getInt("UserID");
			}
			System.out.println("found ID: " + userID);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				rs.close();
				prepStmt.close();
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return userID;
	}
// Read user from DB by user handle (screenname)
	public static int getUserByHandle(String handle) {
		int userID = -1;
		Connection conn = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			conn = connect();
			prepStmt = conn.prepareStatement("SELECT UserID FROM Users WHERE Handle = ?");
			prepStmt.setString(1, handle);
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				userID = rs.getInt("UserID");
			}
			System.out.println("found ID: " + userID);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				rs.close();
				prepStmt.close();
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return userID;
	}
	
// return users who are followed	
public static ArrayList<User> followedUsers(int userID) {
	ArrayList<User> followedUsers = new ArrayList<User>();
	Connection conn = null;
	PreparedStatement prepStmt = null;
	ResultSet rs = null;
	
	String sqlString = "Select * FROM Users, Followers WHERE Followers.FollowedByUserID = " + 
			userID + " AND Users.UserID = Followers.FollowedUserID AND Users.UserID != " + userID;
	try {
		conn = connect();
		prepStmt = conn.prepareStatement("Select * FROM Users, Followers WHERE Followers.FollowedByUserID = ? " +
				"AND Users.UserID = Followers.FollowedUserID AND Users.UserID != ?");
		prepStmt.setInt(1, userID);
		prepStmt.setInt(2, userID);
		rs = prepStmt.executeQuery();
		while (rs.next()) {
			System.out.println("adding user");
			User u = new User(rs.getString("UserName"), "", rs.getString("handle"), rs.getInt("UserID"));
			followedUsers.add(u);
		}
	} catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	return followedUsers;
}
// return users who are not followed
public static ArrayList<User> unfollowedUsers(int userID) {
	ArrayList<User> unfollowedUsers = new ArrayList<User>();
	Connection conn = null;
	PreparedStatement prepStmt = null;
	ResultSet rs = null;
	try {
		conn = connect();
		prepStmt = conn.prepareStatement(
				"Select * FROM Users WHERE UserID NOT IN " + 
						"(SELECT UserID FROM Users, Followers WHERE " + 
						"Followers.FollowedByUserID = ? AND Users.UserID = Followers.FollowedUserID)");
		prepStmt.setInt(1, userID);
		rs = prepStmt.executeQuery();
		while (rs.next()) {
			System.out.println("adding user");
			User u = new User(rs.getString("UserName"), "", rs.getString("handle"), rs.getInt("UserID"));
			unfollowedUsers.add(u);
		}
	} catch (SQLException e) {
		System.out.println(e.getMessage());
	} finally {
		try {
			rs.close();
			prepStmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	return unfollowedUsers;
}
}