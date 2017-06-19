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
	int tweetID;
	String tweet;
	int timeStamp;
	String tserID;
	String replyText;

	public Tweet(int TweetID, String Tweet, int TimeStamp, String UserID) {
		this.tweetID = TweetID;
		this.tweet = Tweet;
		this.timeStamp = TimeStamp;
		this.tserID = UserID;
	}

	Tweet() {

	}
	

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

	// Method to insert replies against a tweet
	public void insertReply(int tweetID, int userID, String replyText) {

		LocalDateTime TimeStamp = LocalDateTime.now();
		String date = TimeStamp.toString();
		String sql = "INSERT INTO Replies(TweetID,UserID,TimeStamp,ReplyText) VALUES(?,?,?,?)";
		System.out.println("sql stmt1 " + sql);
		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			System.out.println("sql stmt2 " + sql);
			pstmt.setDouble(1, tweetID);
			pstmt.setDouble(2, userID);
			pstmt.setString(3, date);
			pstmt.setString(4, replyText);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	//this method is for getting tweets
	public ArrayList<String> get(int userID, String get) {
		ArrayList tweetList = new ArrayList();

		String sql;
		String resText = "";
		if (get == "self") {
			sql = "SELECT Tweet FROM Tweets where UserID=" + userID;
		} else {
			sql = "SELECT * FROM Tweets where UserID in "
					+ "(select FollowedByUserID from Followers where FollowedUserID =" + userID + ")";
		}
		// System.out.println(sql);
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// loop through the result set
			while (rs.next()) {
				Tweet t = new Tweet(rs.getInt("TweetID"), rs.getString("Tweet"), rs.getInt("UserID"), "");
				tweetList.add(t);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		// System.out.println(tweetList.get(0));
		// return tweetList;
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
