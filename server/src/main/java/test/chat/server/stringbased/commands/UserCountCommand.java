package test.chat.server.stringbased.commands;

import test.chat.server.Client;
import test.chat.server.Chat;
import test.chat.server.stringbased.CommandExecutionResult;
import test.chat.server.stringbased.CommandHandler;

public class UserCountCommand implements CommandHandler {
	private Chat<String> chat;

	public UserCountCommand(Chat<String> chat) {
		this.chat = chat;
	}

	@Override
	public CommandExecutionResult executeCommand(Client<String> client, String command, String args) {
		chat.sendSystemMessage(client, "Number of users: " + chat.getActiveClientCount());
		return CommandExecutionResult.SUCCESS;
	}
}
