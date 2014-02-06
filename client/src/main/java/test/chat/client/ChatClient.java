package test.chat.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.protocol.MessageProtocol;
import test.chat.protocol.MessageReader;
import test.chat.protocol.MessageWriter;
import test.chat.util.CloseableUtils;
import test.chat.util.Updateable;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static test.chat.client.ChatConnectionListener.CloseListener;
import static test.chat.client.ChatConnectionListener.ConnectListener;
import static test.chat.client.ChatConnectionListener.DataListener;

public class ChatClient<MessageType> implements Closeable, Updateable {
	private static final Logger logger = LogManager.getLogger();

	private ChatConnection chatConnection;
	private MessageProtocol<MessageType> protocol;
	private MessageReader<MessageType> reader;
	private MessageWriter<MessageType> writer;
	private Queue<MessageType> outgoingMessages = new ConcurrentLinkedQueue<>();
	private ChatOutput<MessageType> output;
	private boolean closed;

	public ChatClient(ChatConnection chatConnection, MessageProtocol<MessageType> protocol) {
		if (chatConnection == null) {
			throw new IllegalArgumentException();
		}
		if (protocol == null) {
			throw new IllegalArgumentException();
		}
		this.chatConnection = chatConnection;
		this.protocol = protocol;
		reader = protocol.getMessageReader();
		setupWriter();
		chatConnection.setCloseListener(createCloseListener());
		chatConnection.setDataListener(createDataListener());
		output = ChatOutput.nullOutput();
	}

	private void setupWriter() {
		if (chatConnection.isConnected()) {
			createWriter();
		} else {
			chatConnection.setConnectListener(createConnectListener());
		}
	}

	private void createWriter() {
		try {
			writer = protocol.getMessageWriter(chatConnection.getOutputChannel());
		} catch (IOException e) {
			logger.catching(e);
			writer = new MessageWriter<MessageType>() {
				@Override
				public void write(MessageType s) throws IOException {
					logger.error("Cannot write to server");
				}
			};
		}
	}

	public void setOutput(ChatOutput<MessageType> output) {
		if (output == null) {
			throw new IllegalArgumentException("Parameter 'output' is null");
		}
		this.output = output;
	}

	public void sendMessage(MessageType message) throws IOException {
		outgoingMessages.add(message);
	}

	@Override
	public void update() throws IOException {
		if (closed) {
			throw new IOException("Client is closed");
		}
		sendMessages();
		chatConnection.update();
	}

	private ConnectListener createConnectListener() {
		return new ConnectListener() {
			@Override
			public void onConnect(ChatConnection connection) {
				logger.entry();
				createWriter();
				logger.exit();
			}
		};
	}

	private CloseListener createCloseListener() {
		return new CloseListener() {
			@Override
			public void onClose(ChatConnection connection) {
				logger.entry();
				CloseableUtils.closeSilently(ChatClient.this);
				logger.exit();
			}
		};
	}

	private DataListener createDataListener() {
		return new DataListener() {
			@Override
			public void onData(ChatConnection connection) {
				logger.entry();
				try {
					List<MessageType> messages = reader.read(connection.getInputChannel());
					for (MessageType message : messages) {
						output.print(message);
					}
				} catch (IOException e) {
					logger.catching(e);
					CloseableUtils.closeSilently(connection);
				}
				logger.exit();
			}
		};
	}

	private void sendMessages() throws IOException {
		if (writer != null) {
			MessageType message;
			while ((message = outgoingMessages.poll()) != null) {
				writer.write(message);
			}
		}
	}

	@Override
	public void close() throws IOException {
		if (!closed) {
			closed = true;
			chatConnection.close();
		}
	}
}
