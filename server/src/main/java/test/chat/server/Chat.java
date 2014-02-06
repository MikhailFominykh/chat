package test.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.util.CloseableUtils;

import java.io.IOException;
import java.util.*;

public abstract class Chat<MessageType> implements ClientRegistry<MessageType> {
	public static final Logger logger = LogManager.getLogger();
	public static final String SYSTEM_NAME = "System";

	private Set<Client<MessageType>> anonymousClients = new HashSet<>();
	private Set<Client<MessageType>> activeClients = new HashSet<>();
	private Map<String, Client<MessageType>> clientByName = new HashMap<>();

	private MessageProcessor<MessageType> anonymousClientMessageProcessor = MessageProcessor.nullProcessor();
	private MessageProcessor<MessageType> activeClientMessageProcessor = MessageProcessor.nullProcessor();

	private MessageHistory<MessageType> messageHistory = MessageHistory.nullHistory();

	public void setMessageHistory(MessageHistory<MessageType> messageHistory) {
		this.messageHistory = messageHistory;
	}

	public void setAnonymousClientMessageProcessor(MessageProcessor<MessageType> anonymousClientMessageProcessor) {
		this.anonymousClientMessageProcessor = anonymousClientMessageProcessor;
	}

	public void setActiveClientMessageProcessor(MessageProcessor<MessageType> activeClientMessageProcessor) {
		this.activeClientMessageProcessor = activeClientMessageProcessor;
	}

	@Override
	public void addClient(Client<MessageType> client) {
		anonymousClients.add(client);
		client.setClientRegistry(this);
		client.setMessageProcessor(anonymousClientMessageProcessor);
		sendWelcomeMessage(client);
	}

	protected abstract void sendWelcomeMessage(Client<MessageType> client);

	@Override
	public void removeClient(Client<MessageType> client) {
		CloseableUtils.closeSilently(client);
		if (anonymousClients.contains(client)) {
			anonymousClients.remove(client);
		} else {
			activeClients.remove(client);
			clientByName.remove(client.getName());
		}
	}

	public void activateClient(Client<MessageType> client) {
		if (anonymousClients.contains(client)) {
			anonymousClients.remove(client);
			activeClients.add(client);
			client.setMessageProcessor(activeClientMessageProcessor);
			sendHistory(client);
		}
	}

	private void sendHistory(Client<MessageType> client) {
		for (MessageType message : messageHistory.getMessages()) {
			sendMessage(client, message);
		}
	}

	public void sendMessage(Client<MessageType> client, MessageType message) {
		try {
			client.send(message);
		} catch (IOException e) {
			logger.catching(e);
			removeClient(client);
		}
	}

	public abstract void sendSystemMessage(Client<MessageType> client, MessageType message);

	public void sendToAll(MessageType message) {
		messageHistory.add(message);
		for (Client<MessageType> client : activeClients) {
			sendMessage(client, message);
		}
	}

	public boolean hasClient(String newName) {
		return clientByName.containsKey(newName);
	}

	public void setClientName(Client<MessageType> client, String name) {
		if (client.getName() != null) {
			clientByName.remove(client.getName());
		}
		client.setName(name);
		clientByName.put(name, client);
	}

	public Client<MessageType> getClient(String name) {
		return clientByName.get(name);
	}

	public Collection<String> getClientNames() {
		return Collections.unmodifiableSet(clientByName.keySet());
	}

	public int getActiveClientCount() {
		return activeClients.size();
	}
}
