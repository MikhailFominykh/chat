package test.chat.server;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ConnectionServerTest {
	@Test
	public void should_accept_connections1() throws Exception {
		ChannelConsumer channelConsumer = Mockito.mock(ChannelConsumer.class);
		ConnectionServer connectionServer = new ConnectionServer(channelConsumer);
		connectionServer.start(new InetSocketAddress(InetAddress.getLocalHost(), 0));
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(connectionServer.getLocalAddress());
		connectionServer.update();
		Mockito.verify(channelConsumer).addChannel(Mockito.any(SocketChannel.class));
	}
}
