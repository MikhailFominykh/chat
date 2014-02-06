package test.chat.server;

import org.mockito.Mockito;
import org.testng.annotations.Test;
import test.chat.server.stringbased.ChatCommands;
import test.chat.server.stringbased.CommandHandler;

import static org.mockito.Mockito.mock;

public class ChatCommandsTest {

	@Test
	public void should_call_registered_command() throws Exception {
		ChatCommands commands = new ChatCommands();
		CommandHandler commandHandler = mock(CommandHandler.class);
		String commandName = "test_command";
		String commandArgs = "command args";
		commands.registerCommand(commandName, commandHandler);
		Client client = mock(Client.class);
		commands.executeCommand(client, commandName, commandArgs);
		Mockito.verify(commandHandler).executeCommand(client, commandName, commandArgs);
	}
}
