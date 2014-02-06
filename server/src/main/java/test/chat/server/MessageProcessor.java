package test.chat.server;

public abstract class MessageProcessor<MessageType> {
	public static final MessageProcessor NULL = new NullProcessor();

	@SuppressWarnings("unchecked")
	public static <MessageType> MessageProcessor<MessageType> nullProcessor() {
		return (MessageProcessor<MessageType>) NULL;
	}

	private static class NullProcessor<MessageType> extends MessageProcessor<MessageType> {
		@Override
		public void processMessage(Client<MessageType> client, MessageType message) {
		}
	}

	public abstract void processMessage(Client<MessageType> client, MessageType message);
}
