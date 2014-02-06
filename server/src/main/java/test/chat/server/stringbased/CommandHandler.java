package test.chat.server.stringbased;

import test.chat.server.Client;

public interface CommandHandler {
	public static final CommandHandler NULL = new CommandHandler() {
		@Override
		public CommandExecutionResult executeCommand(Client<String> client, String command, String args) {
			return CommandExecutionResult.COMMAND_NOT_FOUND;
		}
	};

	public abstract CommandExecutionResult executeCommand(Client<String> client, String command, String args);
}
