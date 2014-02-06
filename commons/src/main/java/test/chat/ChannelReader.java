package test.chat;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface ChannelReader {
	public static final ChannelReader NULL = new ChannelReader() {
		@Override
		public void read(SelectionKey key) throws IOException {
		}
	};

	public void read(SelectionKey key) throws IOException;
}
