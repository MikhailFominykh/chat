package test.chat.server;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class LimitedMessageHistoryTest {
	List<String> messages = Arrays.asList("one", "two", "three", "four", "five");

	@Test
	public void should_return_correct_history() throws Exception {
		LimitedMessageHistory<String> history = new LimitedMessageHistory<>(10);
		for (String message : messages) {
			history.add(message);
		}
		Assert.assertEquals(history.getMessages(), messages);
	}

	@Test
	public void should_respect_limit() throws Exception {
		LimitedMessageHistory<String> history = new LimitedMessageHistory<>(3);
		for (String message : messages) {
			history.add(message);
		}
		Assert.assertEquals(history.getMessages(), Arrays.asList("three", "four", "five"));
	}
}
