package test.chat.client;

public interface ChatConnectionListener {
	public static interface ConnectListener {
		public static ConnectListener NULL = new ConnectListener() {
			@Override
			public void onConnect(ChatConnection connection) {
			}
		};

		public void onConnect(ChatConnection connection);
	}

	public static interface CloseListener {
		public static CloseListener NULL = new CloseListener() {
			@Override
			public void onClose(ChatConnection connection) {
			}
		};

		public void onClose(ChatConnection connection);
	}

	public static interface DataListener {
		public static DataListener NULL = new DataListener() {
			@Override
			public void onData(ChatConnection connection) {
			}
		};

		public void onData(ChatConnection connection);
	}
}
