package test.chat.server.stringbased;

import test.chat.server.Client;

public interface CommandHandler {
	public abstract CommandExecutionResult executeCommand(Client<String> client, String command, String args);
}
