package test.chat.server.stringbased;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.server.Client;
import test.chat.server.MessageProcessor;

public class ActiveClientMessageProcessor extends MessageProcessor<String> {
	private static final Logger logger = LogManager.getLogger();
	private static final String COMMAND_PREFIX = "/";

	private CommandHandler commandHandler;
	private Chat chat;

	public ActiveClientMessageProcessor(CommandHandler commandHandler, Chat chat) {
		this.commandHandler = commandHandler;
		this.chat = chat;
	}

	@Override
	public void processMessage(Client<String> client, String message) {
		logger.trace("{}: >>{}<<", client.getName(), message);
		if (isCommand(message)) {
			executeCommand(client, message);
		} else {
			sendToAll(client, message);
		}
	}

	private boolean isCommand(String message) {
		return message.startsWith(COMMAND_PREFIX);
	}

	private void executeCommand(Client<String> client, String message) {
		String commandName;
		String args;
		int i = message.indexOf(" ");
		if (i == -1) {
			commandName = message.substring(COMMAND_PREFIX.length());
			args = "";
		} else {
			commandName = message.substring(COMMAND_PREFIX.length(), i);
			args = message.substring(i + 1);
		}
		commandHandler.executeCommand(client, commandName, args);
	}

	private void sendToAll(Client<String> client, String message) {
		StringBuilder sb = new StringBuilder(client.getName());
		sb.append(": ").append(message);
		chat.sendToAll(sb.toString());
	}
}
