package test.chat.server.stringbased.commands;

import test.chat.server.Client;
import test.chat.server.stringbased.Chat;
import test.chat.server.stringbased.CommandExecutionResult;
import test.chat.server.stringbased.CommandHandler;

public class KickUserCommand implements CommandHandler {
	private Chat chat;

	public KickUserCommand(Chat chat) {
		this.chat = chat;
	}

	@Override
	public CommandExecutionResult executeCommand(Client<String> client, String command, String args) {
		String name = extractName(args);
		if (chat.hasClient(name)) {
			chat.removeClient(chat.getClient(name));
			return CommandExecutionResult.SUCCESS;
		} else {
			chat.sendSystemMessage(client, "User \"" + name + "\" not found");
		}
		return CommandExecutionResult.ERROR;
	}

	private String extractName(String args) {
		String trimmed = args.trim();
		int i = trimmed.indexOf(" ");
		if (i == -1) {
			return trimmed;
		}
		return trimmed.substring(0, i);
	}

}
