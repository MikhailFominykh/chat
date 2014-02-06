package test.chat.client;

public abstract class ChatOutput<T> {
	public static <T> ChatOutput<T> nullOutput() {
		return new ChatOutput<T>() {
			@Override
			public void print(T s) {
			}
		};
	}

	public abstract void print(T s);
}
