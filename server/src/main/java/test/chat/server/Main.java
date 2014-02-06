package test.chat.server;

import test.chat.protocol.DefaultProtocol;
import test.chat.protocol.Utf8StringMessageProtocol;
import test.chat.server.stringbased.ActiveClientMessageProcessor;
import test.chat.server.stringbased.AnonymousClientMessageProcessor;
import test.chat.server.stringbased.ChatCommands;
import test.chat.server.stringbased.StringBasedChat;
import test.chat.server.stringbased.commands.ChangeNameCommand;
import test.chat.server.stringbased.commands.KickUserCommand;
import test.chat.server.stringbased.commands.UserCountCommand;
import test.chat.server.stringbased.commands.UserListCommand;
import test.chat.util.Runner;
import test.chat.util.Updateable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class Main {

	public static final String PARAM_PORT = "chat.port";

	public static void main(String[] args) throws IOException, StartedServerException {
		Utf8StringMessageProtocol protocol = new Utf8StringMessageProtocol(new DefaultProtocol());

		Chat<String> chat = new StringBasedChat();
		ChatCommands chatCommands = createChatCommands(chat);

		chat.setMessageHistory(new LimitedMessageHistory<String>(100));
		chat.setAnonymousClientMessageProcessor(new AnonymousClientMessageProcessor(chatCommands, chat));
		chat.setActiveClientMessageProcessor(new ActiveClientMessageProcessor(chatCommands, chat));

		final ClientSocketProcessor socketProcessor = new ClientSocketProcessor();
		socketProcessor.init();

		final SocketClientProducer<String> clientProducer = new SocketClientProducer<>(protocol, chat, socketProcessor);

		Updateable chatUpdater = new Updateable() {
			@Override
			public void update() throws Exception {
				clientProducer.update();
				socketProcessor.update();
			}
		};

		new Thread(new Runner(chatUpdater, 10)).start();

		ConnectionServer connectionServer = new ConnectionServer(clientProducer);
		connectionServer.start(getAddress());
		new Thread(new Runner(connectionServer, 10)).start();
	}

	private static ChatCommands createChatCommands(Chat chat) {
		ChatCommands chatCommands = new ChatCommands();
		chatCommands.registerCommand("name", new ChangeNameCommand(chat));
		chatCommands.registerCommand("users", new UserListCommand(chat));
		chatCommands.registerCommand("usercount", new UserCountCommand(chat));
		chatCommands.registerCommand("kick", new KickUserCommand(chat));
		return chatCommands;
	}

	private static SocketAddress getAddress() throws UnknownHostException {
		int port = Integer.getInteger(PARAM_PORT, 0);
		return new InetSocketAddress(InetAddress.getLocalHost(), port);
	}
}
