package test.chat.server.stringbased;

import test.chat.server.Chat;
import test.chat.server.Client;

public class StringBasedChat extends Chat<String> {
	public static final String WELCOME_MESSAGE = "Welcome, anonymous. Enter you name.";

	@Override
	protected void sendWelcomeMessage(Client<String> client) {
		sendMessage(client, WELCOME_MESSAGE);
	}

	@Override
	public void sendSystemMessage(Client<String> client, String message) {
		StringBuilder sb = new StringBuilder(SYSTEM_NAME);
		sb.append(": ").append(message);
		sendMessage(client, sb.toString());
	}
}
