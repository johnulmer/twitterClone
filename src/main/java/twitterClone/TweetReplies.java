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

public class TweetReplies {
	int tweetID;
	String tweet;
	int timeStamp;
	String tserID;
	String replyText;

	public TweetReplies(String replytext) {
		this.replyText = replytext;
	}

	TweetReplies() {

	}
	

	// in progress here RAVI
	public ArrayList<String> getreply(int tweetID) {
		ArrayList replylist = new ArrayList();

		String sql;
		String resText = "";
		sql = "SELECT ReplyText FROM Replies where TweetID=" + tweetID;
		// System.out.println(sql);
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// loop through the result set
			while (rs.next()) {
				TweetReplies t = new TweetReplies(rs.getString("ReplyText"));
				replylist.add(t);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		// System.out.println(tweetList.get(0));
		// return tweetList;
		return replylist;
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
