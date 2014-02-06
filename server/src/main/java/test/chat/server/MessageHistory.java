package test.chat.server;

import java.util.Collections;
import java.util.List;

public abstract class MessageHistory<MessageType> {
	public static final MessageHistory NULL = new NullHistory();

	@SuppressWarnings("unchecked")
	public static <MessageType> MessageHistory<MessageType> nullHistory() {
		return (MessageHistory<MessageType>) NULL;
	}

	private static class NullHistory<MessageType> extends MessageHistory<MessageType> {
		@Override
		public void add(MessageType message) {
		}

		@Override
		public List<MessageType> getMessages() {
			return Collections.emptyList();
		}
	}

	public abstract void add(MessageType message);

	public abstract List<MessageType> getMessages();
}
