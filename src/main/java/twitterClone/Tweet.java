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
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Tweet {
	int tweetID;
	String tweet;
	String timeStamp;
	String userName;
	String replyText;
	int likeCount;

	public Tweet(int TweetID, String Tweet, String TimeStamp, String userName,int likeCount) {
		this.tweetID = TweetID;
		this.tweet = Tweet;
		this.timeStamp = TimeStamp;
		this.userName = userName;
		this.likeCount=likeCount;
	}

	Tweet() {

	}

	public void insert(String Tweet, int UserID, String Image) {

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
			pstmt.setDouble(1, tweetID);
			pstmt.setDouble(2, userID);
			pstmt.setString(3, date);
			pstmt.setString(4, replyText);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// this method is for getting tweets
	public ArrayList<Tweet> get(int userID, String reqType) {
		ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
		String sql="";
		String resText = "";
		if (reqType == "own") {
			sql = "SELECT * FROM Tweets where UserID=" + userID + " order by TimeStamp desc" ;
		} else if (reqType == "main") {
			sql = "SELECT * FROM Tweets where UserID in "
					+ "(select FollowedUserID from Followers where FollowedByUserID =" + userID + " AND BlockFollowingUser IS NULL )" + " order by TimeStamp desc";
		}
		 System.out.println(sql);
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// loop through the result set
			while (rs.next()) {
				User u = new User(rs.getInt("UserID"));
				userName = u.getUserName();
				SimpleDateFormat formatter, FORMATTER;
				formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				String oldDate = rs.getString("TimeStamp");
				Date date;
				try {
						date = formatter.parse(oldDate.substring(0, 23));
						FORMATTER = new SimpleDateFormat("MMM d");
						timeStamp=FORMATTER.format(date);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
//				getLikeCount();
				Likes likeClass = new Likes();
				int likeCount = likeClass.getLikeCount(rs.getInt("TweetID"));
				Tweet t = new Tweet(rs.getInt("TweetID"), rs.getString("Tweet"),timeStamp ,userName,likeCount);
				tweetList.add(t);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		// System.out.println(tweetList.get(0));
		// return tweetList;
		return tweetList;
	}

//	USED
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
