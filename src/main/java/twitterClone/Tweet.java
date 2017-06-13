package twitterClone;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Tweet {

	public void insert(int TweetID, String Tweet, String TimeStamp, int UserID, String Image) {
		String sql = "INSERT INTO Tweets(TweetID,Tweet,TimeStamp,UserID,Image) VALUES(?,?,?,?,?)";

		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setDouble(1, TweetID);
			pstmt.setString(2, Tweet);
			pstmt.setString(3, TimeStamp);
			pstmt.setDouble(4, UserID);
			pstmt.setString(5, Image);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	Tweet(int UserID, String Tweet) {
		int TweetID = 1;
		String Image = " ";
		if (Tweet == "") { // equals
			System.out.println("Nothing entered in tweet message");
		} else {
			LocalDateTime TimeStamp = LocalDateTime.now();
			String date = TimeStamp.toString();
			insert(TweetID, Tweet, date, UserID, Image);

		}

	}

	private Connection connect() {
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
