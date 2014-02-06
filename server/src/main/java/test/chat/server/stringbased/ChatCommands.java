package test.chat.server.stringbased;

import test.chat.server.Client;

import java.util.HashMap;
import java.util.Map;

public class ChatCommands implements CommandHandler {
	private Map<String, CommandHandler> handlers = new HashMap<>();

	@Override
	public CommandExecutionResult executeCommand(Client<String> client, String command, String args) {
		if (handlers.containsKey(command)) {
			return handlers.get(command).executeCommand(client, command, args);
		}
		return CommandExecutionResult.COMMAND_NOT_FOUND;
	}

	public void registerCommand(String commandName, CommandHandler handler) {
		handlers.put(commandName, handler);
	}
}
