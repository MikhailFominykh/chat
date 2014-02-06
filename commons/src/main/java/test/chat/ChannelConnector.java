package test.chat;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface ChannelConnector {
	public static final ChannelConnector NULL = new ChannelConnector() {
		@Override
		public void connect(SelectionKey key) {
		}
	};

	public void connect(SelectionKey key) throws IOException;
}
