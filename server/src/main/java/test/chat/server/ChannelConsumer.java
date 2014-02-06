package test.chat.server;

import java.nio.channels.SocketChannel;

public interface ChannelConsumer {
	public void addChannel(SocketChannel channel);
}
