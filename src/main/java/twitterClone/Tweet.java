package twitterClone;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Tweet {

	public void insert(int TweetID, String Tweet, int UserID, String Image) {

		LocalDateTime TimeStamp = LocalDateTime.now();
		String date = TimeStamp.toString();

		String sql = "INSERT INTO Tweets(Tweet,TimeStamp,UserID,Image) VALUES(?,?,?,?)";

		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, Tweet);
			pstmt.setString(2, date);
			pstmt.setDouble(3, UserID);
			pstmt.setString(4, Image);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public ArrayList<String> get(int userID, String get) {
		ArrayList<String> tweetList = new ArrayList<String>();
		String sql;
		String resText = "";
//		String sql = "SELECT Tweet FROM Tweets where UserID=" + userID;
		if (get =="self") {
			sql = "SELECT Tweet FROM Tweets where UserID=" + userID;
		} else {
			sql = "SELECT Tweet FROM Tweets where UserID in "
				+ "(select FollowedByUserID from Followers where FollowedUserID =" + userID + ")";
		}
		System.out.println(sql);
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// loop through the result set
			while (rs.next()) {
				tweetList.add(rs.getString("Tweet"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
//		return tweetList;
		return tweetList; 
	}

	public Connection connect() {
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

}
