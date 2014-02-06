package test.chat.server.stringbased.commands;

import test.chat.server.Client;
import test.chat.server.stringbased.Chat;
import test.chat.server.stringbased.CommandExecutionResult;
import test.chat.server.stringbased.CommandHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserListCommand implements CommandHandler {
	private Chat chat;

	public UserListCommand(Chat chat) {
		this.chat = chat;
	}

	@Override
	public CommandExecutionResult executeCommand(Client<String> client, String command, String args) {
		List<String> clientNames = new ArrayList<>(chat.getClientNames());
		Collections.sort(clientNames);
		chat.sendSystemMessage(client, "Users list:");
		for (String clientName : clientNames) {
			chat.sendSystemMessage(client, clientName);
		}
		return CommandExecutionResult.SUCCESS;
	}
}
