package test.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.protocol.MessageProtocol;
import test.chat.protocol.MessageReader;
import test.chat.protocol.MessageWriter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.util.List;

public class Client<MessageType> implements Closeable {
	public static final Logger logger = LogManager.getLogger();

	private ByteChannel channel;
	private MessageReader<MessageType> reader;
	private MessageWriter<MessageType> writer;
	private MessageProcessor<MessageType> messageProcessor = MessageProcessor.nullProcessor();
	private String name;
	private ClientRegistry<MessageType> clientRegistry;

	public Client(ByteChannel channel, MessageProtocol<MessageType> protocol) {
		if (channel == null) {
			throw new IllegalArgumentException("Parameter 'channel' is null");
		}
		if (protocol == null) {
			throw new IllegalArgumentException("Parameter 'protocol' is null");
		}
		this.channel = channel;
		reader = protocol.getMessageReader();
		writer = protocol.getMessageWriter(channel);
	}

	public void setClientRegistry(ClientRegistry<MessageType> clientRegistry) {
		if (clientRegistry == null) {
			throw new IllegalArgumentException("Parameter 'clientRegistry' is null");
		}
		this.clientRegistry = clientRegistry;
	}

	public void setMessageProcessor(MessageProcessor<MessageType> messageProcessor) {
		if (messageProcessor == null) {
			throw new IllegalArgumentException("Parameter 'messageProcessor' is null");
		}
		this.messageProcessor = messageProcessor;
	}

	public void read() {
		try {
			List<MessageType> messages = reader.read(channel);
			for (MessageType message : messages) {
				messageProcessor.processMessage(this, message);
			}
		} catch (IOException e) {
			logger.catching(e);
			clientRegistry.removeClient(this);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void send(MessageType message) throws IOException {
		writer.write(message);
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}
}
