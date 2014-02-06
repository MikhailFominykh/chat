package test.chat.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.ChannelConnector;
import test.chat.ChannelReader;
import test.chat.SelectorProcessor;
import test.chat.util.CloseableUtils;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

import static test.chat.client.ChatConnectionListener.CloseListener;
import static test.chat.client.ChatConnectionListener.ConnectListener;
import static test.chat.client.ChatConnectionListener.DataListener;

public class SocketChatConnection implements ChatConnection {
	private static final Logger logger = LogManager.getLogger();

	private boolean closed;
	private SelectorProcessor selectorProcessor;
	private SocketChannel socketChannel;
	private ConnectListener connectListener = ConnectListener.NULL;
	private CloseListener closeListener = CloseListener.NULL;
	private DataListener dataListener = DataListener.NULL;

	@Override
	public boolean isConnected() {
		return socketChannel != null && socketChannel.isConnected();
	}

	@Override
	public WritableByteChannel getOutputChannel() throws IOException {
		if (socketChannel == null) {
			throw new IOException("Not connected");
		}
		return socketChannel;
	}

	@Override
	public ReadableByteChannel getInputChannel() throws IOException {
		if (socketChannel == null) {
			throw new IOException("Not connected");
		}
		return socketChannel;
	}

	@Override
	public void setConnectListener(ConnectListener connectListener) {
		if (connectListener == null) {
			throw new IllegalArgumentException();
		}
		this.connectListener = connectListener;
	}

	@Override
	public void setCloseListener(CloseListener closeListener) {
		if (closeListener == null) {
			throw new IllegalArgumentException();
		}
		this.closeListener = closeListener;
	}

	@Override
	public void setDataListener(DataListener dataListener) {
		if (dataListener == null) {
			throw new IllegalArgumentException();
		}
		this.dataListener = dataListener;
	}

	public void connect(SocketAddress address) throws IOException {
		if (closed) {
			throw new ClosedChannelException();
		}
		if (selectorProcessor != null) {
			throw new AlreadyConnectedException();
		}
		selectorProcessor = new SelectorProcessor(Selector.open());
		selectorProcessor.setConnector(createConnector());
		selectorProcessor.setReader(createReader());
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.register(selectorProcessor.getSelector(), SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
			socketChannel.connect(address);
		} catch (IOException e) {
			CloseableUtils.closeSilently(socketChannel);
			CloseableUtils.closeSilently(selectorProcessor);
			socketChannel = null;
			selectorProcessor = null;
			throw e;
		}
	}

	private ChannelConnector createConnector() {
		return new ChannelConnector() {
			@Override
			public void connect(SelectionKey key) throws IOException {
				assert socketChannel != null;
				assert connectListener != null;

				if (socketChannel.finishConnect()) {
					logger.debug("Connected to server at {}", socketChannel.getRemoteAddress());
					connectListener.onConnect(SocketChatConnection.this);
				} else {
					logger.debug("Cannot connect to server");
					CloseableUtils.closeSilently(SocketChatConnection.this);
				}
			}
		};
	}

	private ChannelReader createReader() {
		return new ChannelReader() {
			@Override
			public void read(SelectionKey key) throws IOException {
				assert dataListener != null;

				dataListener.onData(SocketChatConnection.this);
			}
		};
	}

	@Override
	public void close() throws IOException {
		assert closeListener != null;

		if (!closed) {
			CloseableUtils.closeSilently(socketChannel);
			CloseableUtils.closeSilently(selectorProcessor);
			socketChannel = null;
			selectorProcessor = null;
			closed = true;
			closeListener.onClose(this);
			logger.debug("Closed");
		}
	}

	public void update() throws IOException {
		if (selectorProcessor == null) {
			throw new IOException("Illegal state");
		}
		selectorProcessor.update();
	}
}
