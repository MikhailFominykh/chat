package test.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.ChannelAcceptor;
import test.chat.SelectorProcessor;
import test.chat.util.CloseableUtils;
import test.chat.util.Updateable;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionServer implements Updateable {
	private static final Logger logger = LogManager.getLogger();

	private ChannelConsumer channelConsumer;
	private SelectorProcessor selectorProcessor;
	private ServerSocketChannel socketChannel;
	private boolean started;

	public ConnectionServer(ChannelConsumer channelConsumer) {
		this.channelConsumer = channelConsumer;
	}

	public void start(SocketAddress address) throws IOException, StartedServerException {
		if (address == null) {
			throw new IllegalArgumentException("Parameter 'address' is null");
		}
		if (started) {
			throw new StartedServerException();
		}
		try {
			selectorProcessor = new SelectorProcessor(Selector.open());
			selectorProcessor.setAcceptor(createChannelAcceptor());
			socketChannel = ServerSocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.bind(address);
			socketChannel.register(selectorProcessor.getSelector(), SelectionKey.OP_ACCEPT);
			started = true;
			logger.info("Connection server started at {}", socketChannel.getLocalAddress());
		} catch (IOException e) {
			close();
			throw e;
		}
	}

	private ChannelAcceptor createChannelAcceptor() {
		return new ChannelAcceptor() {
			@Override
			public void accept(SelectionKey key) throws IOException {
				SocketChannel channel;
				while ((channel = socketChannel.accept()) != null) {
					channelConsumer.addChannel(channel);
				}
			}
		};
	}

	private void close() {
		CloseableUtils.closeSilently(socketChannel);
		CloseableUtils.closeSilently(selectorProcessor);
		socketChannel = null;
		selectorProcessor = null;
		started = false;
	}

	public void update() throws IOException {
		if (started) {
			selectorProcessor.update();
		}
	}

	public SocketAddress getLocalAddress() throws IOException {
		if (socketChannel == null) {
			throw new IOException("No connection");
		}
		return socketChannel.getLocalAddress();
	}
}
