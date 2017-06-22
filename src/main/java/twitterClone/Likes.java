package twitterClone;

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

public class Likes {
	int tweetID;
	int userID;

	public Likes(int tweetID, int userID) {
		this.tweetID = tweetID;
		this.userID = userID;
	}

	Likes() {

	}
	
//USED
	public void addLike(int TweetID, int UserID) {
		System.out.println("addLike method begins");

		String sql = "INSERT INTO Likes(TweetID,UserID) VALUES(?,?)";

		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setDouble(1, TweetID);
			pstmt.setDouble(2, UserID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

//USED
//	Get like count method
	public int getLikeCount(int TweetID){
//		ArrayList<Likes> likeCountArray = new ArrayList<Likes>();
		String sql;
		int likeCount=0;
		sql = "SELECT  count(*) from Likes where TweetID= "
					 + TweetID + " group by TweetID";
		 
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// loop through the result set
			
			while (rs.next()) {
				likeCount=rs.getInt("count(*)");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}	
		return likeCount;
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
