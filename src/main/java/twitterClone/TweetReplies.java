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
	String timeStamp;
	String tserID;
	String replyText;
	String userName;

	public TweetReplies(String replytext, String timeStamp, String userName) {
		this.replyText = replytext;
		this.timeStamp = timeStamp;
		this.userName = userName;
	}

	TweetReplies() {

	}
	
//USED
	public ArrayList<String> getreply(int tweetID) {
		ArrayList replylist = new ArrayList();

		String sql;
		String resText = "";
		//ravi comehere
		sql = "SELECT ReplyText, TimeStamp, UserID  FROM Replies where TweetID=" + tweetID + " order by TimeStamp desc";
		// System.out.println(sql);
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// loop through the result set
			while (rs.next()) {
				User u = new User(rs.getInt("UserID"));
				userName = u.getUserName();
				TweetReplies t = new TweetReplies(rs.getString("ReplyText"),rs.getString("TimeStamp"),userName);
				replylist.add(t);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		// System.out.println(tweetList.get(0));
		// return tweetList;
		return replylist;
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
