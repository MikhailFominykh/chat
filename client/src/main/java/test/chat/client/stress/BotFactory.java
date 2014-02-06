package test.chat.client.stress;

import java.io.IOException;
import java.net.SocketAddress;

public interface BotFactory {
	public ChatBot createBot(SocketAddress serverAddress) throws IOException;
}
