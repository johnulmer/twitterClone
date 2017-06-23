package twitterClone;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

// Authenticate user based on userName and password
	public static User authenticate(User user) {
		User returnUser = new User(user.getUserName(), user.getPassword(), "", -1);
		Connection conn = null;
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			conn = connect();
			prepStmt = conn.prepareStatement("SELECT UserID, Handle FROM Users WHERE UserName = ? AND Password = ?");
			prepStmt.setString(1, user.getUserName());
			prepStmt.setString(2, user.getPassword());
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				// build return user using username & password from log in screen and handle & ID from DB 
				returnUser = new User(user.getUserName(), user.getPassword(), rs.getString("Handle"), rs.getInt("UserID"));
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
		return returnUser;
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

private static String pwdHash(String plainTextPassword) {
	String returnString = "";
	String SALT = "RaviKumarBhupathiraju";
	plainTextPassword += SALT;
	MessageDigest md = null;
	try {
		md =  MessageDigest.getInstance("SHA");
	} catch (NoSuchAlgorithmException e) {
		System.out.println(e);
	}
	md.update(plainTextPassword.getBytes());
    returnString = new String(md.digest());		
	return returnString;
}

// update a user's handle based on UserID match
public static void updateHandle(int userID, String handle) {
	Connection conn = null;
	PreparedStatement prepStmt = null;
	System.out.println("userID: " + userID + " handle: " + handle);
	try {
		conn = connect();
		prepStmt = conn.prepareStatement("UPDATE Users SET Handle = ? WHERE UserID = ?");
		prepStmt.setString(1, handle);
		prepStmt.setInt(2, userID);
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
}

//update a user's password based on UserID match
public static void updatePassword(int userID, String password) {
	Connection conn = null;
	PreparedStatement prepStmt = null;
	System.out.println("userID: " + userID + " password: " + password);
	try {
		conn = connect();
		prepStmt = conn.prepareStatement("UPDATE Users SET Password = ? WHERE UserID = ?");
		prepStmt.setString(1, password);
		prepStmt.setInt(2, userID);
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
}

//Read user from DB by userID
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
		return u;
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
	
// return users who are followed by a given user	
public static ArrayList<User> followedUsers(int userID) {
	ArrayList<User> followedUsers = new ArrayList<User>();
	Connection conn = null;
	PreparedStatement prepStmt = null;
	ResultSet rs = null;
	try {
		conn = connect();
		prepStmt = conn.prepareStatement("SELECT DISTINCT FollowedUserID "
				+ "FROM Followers WHERE FollowedByUserID = ? "
				+ "AND FollowedUserID != ? "
				+ "AND BlockFollowingUser IS NULL "
				+ "ORDER BY FollowedUserID");
		prepStmt.setInt(1, userID);
		prepStmt.setInt(2, userID);
		rs = prepStmt.executeQuery();
		while (rs.next()) {
			User u = new User(rs.getInt("FollowedUserID"));
			followedUsers.add(u);
		}
	} catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	return followedUsers;
}
// return users who are not followed by a given user
public static ArrayList<User> unfollowedUsers(int userID) {
	ArrayList<User> unfollowedUsers = new ArrayList<User>();
	Connection conn = null;
	PreparedStatement prepStmt = null;
	ResultSet rs = null;
	try {
		conn = connect();
		prepStmt = conn.prepareStatement(
				"Select * FROM Users WHERE UserID NOT IN "
				+ "(SELECT UserID FROM Users, Followers WHERE "
				+ "Followers.FollowedByUserID = ? AND Users.UserID = Followers.FollowedUserID "
				+ "AND BlockFollowingUser IS NULL) "
				+ "ORDER BY UserID");
		prepStmt.setInt(1, userID);
		rs = prepStmt.executeQuery();
		while (rs.next()) {
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

//return users who follow a given user
public static ArrayList<User> myFollowers(int userID) {
	ArrayList<User> myFollowers = new ArrayList<User>();
	Connection conn = null;
	PreparedStatement prepStmt = null;
	ResultSet rs = null;
	try {
		conn = connect();
		prepStmt = conn.prepareStatement("Select * FROM Followers "
				+ "WHERE FollowedUserID = ? "
				+ "AND FollowedByUserID != ? "
				+ "AND BlockFollowingUser IS NULL "
				+ "ORDER BY FollowedByUserID");
		prepStmt.setInt(1, userID);
		prepStmt.setInt(2, userID);
		rs = prepStmt.executeQuery();
		while (rs.next()) {
			User u = new User(rs.getInt("FollowedByUserID"));
			myFollowers.add(u);
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
	return myFollowers;
}

// update Followers table with Followed User and Followed By userIDs
	public static void followAnotherUser(int followedByUserID, int followedUserID) {
		Connection conn = null;
		PreparedStatement prepStmt = null;
		try {
			conn = connect();
			prepStmt = conn.prepareStatement("INSERT INTO Followers (FollowedUserID, FollowedByUserID) VALUES (?, ?)");
			prepStmt.setInt(1, followedUserID);
			prepStmt.setInt(2, followedByUserID);
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
	}
	// update Followers table with Followed User and Followed By userIDs
		public static void stopFollowingUser(int followedByUserID, int UserIDtoStopFollowing) {
			Connection conn = null;
			PreparedStatement prepStmt = null;
			try {
				conn = connect();
				prepStmt = conn.prepareStatement("DELETE FROM Followers WHERE FollowedUserID = ? AND FollowedByUserID = ?");
				prepStmt.setInt(1, UserIDtoStopFollowing);
				prepStmt.setInt(2, followedByUserID);
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
		}

// update Followers table to block a follower	
	public static void blockUser(int blockingUserID, int userIDtoBlock) {
		Connection conn = null;
		PreparedStatement prepStmt = null;
		try {
			conn = connect();
			prepStmt = conn.prepareStatement("UPDATE Followers SET BlockFollowingUser = 'true' "
					+ "WHERE FollowedUserID = ? AND FollowedByUserID = ?");
			prepStmt.setInt(1, blockingUserID);
			prepStmt.setInt(2, userIDtoBlock);
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
		
	}
}