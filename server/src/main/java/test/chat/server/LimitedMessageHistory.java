package test.chat.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LimitedMessageHistory<T> extends MessageHistory<T> {
	private LinkedList<T> messages = new LinkedList<>();
	private int maxHistorySize;

	public LimitedMessageHistory(int maxHistorySize) {
		if (maxHistorySize < 0) {
			throw new IllegalArgumentException("Parameter 'maxHistorySize' is less than zero");
		}
		this.maxHistorySize = maxHistorySize;
	}

	@Override
	public void add(T message) {
		messages.add(message);
		if (messages.size() > maxHistorySize) {
			messages.remove();
		}
	}

	@Override
	public List<T> getMessages() {
		return Collections.unmodifiableList(messages);
	}
}
