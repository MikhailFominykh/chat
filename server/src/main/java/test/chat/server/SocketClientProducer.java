package test.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.protocol.MessageProtocol;
import test.chat.util.Updateable;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SocketClientProducer<MessageType> implements Updateable, ChannelConsumer {
	public static final Logger logger = LogManager.getLogger();

	private Queue<SocketChannel> newChannels = new ConcurrentLinkedDeque<>();
	private ClientRegistry<MessageType> clientRegistry;
	private MessageProtocol<MessageType> protocol;
	private ClientSocketProcessor socketProcessor;

	public SocketClientProducer(MessageProtocol<MessageType> protocol, ClientRegistry<MessageType> clientRegistry, ClientSocketProcessor socketProcessor) {
		this.clientRegistry = clientRegistry;
		this.protocol = protocol;
		this.socketProcessor = socketProcessor;
	}

	@Override
	public void addChannel(SocketChannel channel) {
		newChannels.add(channel);
	}

	@Override
	public void update() throws Exception {
		createNewClients();
	}

	private void createNewClients() {
		SocketChannel channel;
		while ((channel = newChannels.poll()) != null) {
			Client<MessageType> client = createClient(channel);
			try {
				socketProcessor.addClient(client, channel);
				clientRegistry.addClient(client);
			} catch (IOException e) {
				logger.catching(e);
			}
		}
	}

	private Client<MessageType> createClient(SocketChannel channel) {
		return new Client<>(channel, protocol);
	}
}
