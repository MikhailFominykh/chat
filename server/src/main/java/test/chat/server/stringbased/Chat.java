package test.chat.server.stringbased;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.server.Client;
import test.chat.server.ClientRegistry;
import test.chat.server.MessageHistory;
import test.chat.server.MessageProcessor;
import test.chat.util.CloseableUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Chat implements ClientRegistry<String> {
	public static final Logger logger = LogManager.getLogger();
	public static final String WELCOME_MESSAGE = "Welcome, anonymous. Enter you name.";
	public static final String SYSTEM_NAME = "System";

	private Set<Client<String>> anonymousClients = new HashSet<>();
	private Set<Client<String>> activeClients = new HashSet<>();
	private Map<String, Client<String>> clientByName = new HashMap<>();

	private MessageProcessor<String> anonymousClientMessageProcessor = MessageProcessor.nullProcessor();
	private MessageProcessor<String> activeClientMessageProcessor = MessageProcessor.nullProcessor();

	private MessageHistory<String> messageHistory = MessageHistory.nullHistory();

	public void setMessageHistory(MessageHistory<String> messageHistory) {
		this.messageHistory = messageHistory;
	}

	public void setAnonymousClientMessageProcessor(MessageProcessor<String> anonymousClientMessageProcessor) {
		this.anonymousClientMessageProcessor = anonymousClientMessageProcessor;
	}

	public void setActiveClientMessageProcessor(MessageProcessor<String> activeClientMessageProcessor) {
		this.activeClientMessageProcessor = activeClientMessageProcessor;
	}

	@Override
	public void addClient(Client<String> client) {
		anonymousClients.add(client);
		client.setClientRegistry(this);
		client.setMessageProcessor(anonymousClientMessageProcessor);
		sendMessage(client, WELCOME_MESSAGE);
	}

	@Override
	public void removeClient(Client<String> client) {
		CloseableUtils.closeSilently(client);
		if (anonymousClients.contains(client)) {
			anonymousClients.remove(client);
		} else {
			activeClients.remove(client);
			clientByName.remove(client.getName());
		}
	}

	public void activateClient(Client<String> client) {
		if (anonymousClients.contains(client)) {
			anonymousClients.remove(client);
			activeClients.add(client);
			client.setMessageProcessor(activeClientMessageProcessor);
			sendHistory(client);
		}
	}

	private void sendHistory(Client<String> client) {
		for (String message : messageHistory.getMessages()) {
			sendMessage(client, message);
		}
	}

	public void sendMessage(Client<String> client, String message) {
		try {
			client.send(message);
		} catch (IOException e) {
			logger.catching(e);
			removeClient(client);
		}
	}

	public void sendSystemMessage(Client<String> client, String message) {
		StringBuilder sb = new StringBuilder(SYSTEM_NAME);
		sb.append(": ").append(message);
		sendMessage(client, sb.toString());
	}

	public void sendToAll(String message) {
		messageHistory.add(message);
		for (Client<String> client : activeClients) {
			sendMessage(client, message);
		}
	}

	public boolean hasClient(String newName) {
		return clientByName.containsKey(newName);
	}

	public void setClientName(Client<String> client, String name) {
		if (client.getName() != null) {
			clientByName.remove(client.getName());
		}
		client.setName(name);
		clientByName.put(name, client);
	}

	public Client<String> getClient(String name) {
		return clientByName.get(name);
	}

	public Collection<String> getClientNames() {
		return Collections.unmodifiableSet(clientByName.keySet());
	}

	public int getActiveClientCount() {
		return activeClients.size();
	}
}
