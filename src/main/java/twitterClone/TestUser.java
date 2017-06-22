package twitterClone;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestUser {

	@Test
	public void testAdd() {
		User toAdd = new User("TestUName", "TestPassword", "TestHandle");
		toAdd.register();
		User added = new User(TwitterDB.getUserByHandle("TestHandle"));
		assertEquals(toAdd.getUserName(), added.getUserName());
	}

}
