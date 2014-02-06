package test.chat;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface ChannelAcceptor {

	public static final ChannelAcceptor NULL = new ChannelAcceptor() {
		@Override
		public void accept(SelectionKey key) throws IOException {
		}
	};

	public void accept(SelectionKey key) throws IOException;
}
