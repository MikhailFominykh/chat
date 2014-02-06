package test.chat.server.stringbased;

import test.chat.server.Chat;
import test.chat.server.Client;
import test.chat.server.MessageProcessor;
import test.chat.server.stringbased.commands.ChangeNameCommand;

public class AnonymousClientMessageProcessor extends MessageProcessor<String> {
	private CommandHandler commandHandler;
	private Chat<String> chat;

	public AnonymousClientMessageProcessor(CommandHandler commandHandler, Chat<String> chat) {
		this.commandHandler = commandHandler;
		this.chat = chat;
	}

	@Override
	public void processMessage(Client<String> client, String message) {
		CommandExecutionResult result = commandHandler.executeCommand(client, ChangeNameCommand.COMMAND_NAME, message);
		switch (result) {
			case SUCCESS:
				chat.activateClient(client);
				break;
			case ERROR:
				chat.sendSystemMessage(client, "Choose another name.");
				break;
		}
	}
}