package test.chat.client;

import test.chat.protocol.DefaultProtocol;
import test.chat.protocol.Utf8StringMessageProtocol;
import test.chat.util.Runner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class Main {
	public static void main(String[] args) throws IOException {

		SocketChatConnection socketChatConnection = new SocketChatConnection();
		socketChatConnection.connect(getAddress());

		ChatClient<String> chatClient = new ChatClient<>(socketChatConnection, new Utf8StringMessageProtocol(new DefaultProtocol()));
		chatClient.setOutput(new PrintStreamOutput(System.out));

		Thread clientThread = new Thread(new Runner(chatClient, 10));
		clientThread.start();

		new Thread(new StdinReader(chatClient)).start();

		try {
			clientThread.join();
		} catch (InterruptedException e) {
		}
		System.in.close();
	}

	private static SocketAddress getAddress() throws UnknownHostException {
		int port = Integer.getInteger("chat.port", 0);

		InetAddress inetAddress;
		String server = System.getProperty("chat.server");
		if (server == null) {
			inetAddress = InetAddress.getLocalHost();
		} else {
			inetAddress = InetAddress.getByName(server);
		}

		return new InetSocketAddress(inetAddress, port);
	}

}
