package test.chat.server.stringbased.commands;

import test.chat.server.Client;
import test.chat.server.StringUtils;
import test.chat.server.stringbased.Chat;
import test.chat.server.stringbased.CommandExecutionResult;
import test.chat.server.stringbased.CommandHandler;

public class ChangeNameCommand implements CommandHandler {
	public static final String COMMAND_NAME = "name";

	private Chat chat;

	public ChangeNameCommand(Chat chat) {
		this.chat = chat;
	}

	@Override
	public CommandExecutionResult executeCommand(Client<String> client, String command, String args) {
		String newName = StringUtils.getFirstWord(args);
		if (newName.length() == 0) {
			chat.sendSystemMessage(client, "Empty names are not allowed.");
			return CommandExecutionResult.ERROR;
		} else {
			if (Chat.SYSTEM_NAME.equals(newName) || chat.hasClient(newName)) {
				chat.sendSystemMessage(client, "Name \"" + newName + "\" already exists.");
				return CommandExecutionResult.ERROR;
			} else {
				chat.setClientName(client, newName);
				chat.sendSystemMessage(client, "Name has been set: \"" + newName + "\"");
				return CommandExecutionResult.SUCCESS;
			}
		}
	}
}
