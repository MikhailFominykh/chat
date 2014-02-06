package test.chat.client.stress;

import test.chat.client.ChatClient;
import test.chat.client.SocketChatConnection;
import test.chat.protocol.DefaultProtocol;
import test.chat.protocol.Utf8StringMessageProtocol;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultBotFactory implements BotFactory {
	private AtomicInteger botId = new AtomicInteger();

	private long minLifeTime;
	private long maxLifeTime;
	private double messageProbability;
	private int minMessageInterval;
	private int maxMessageInterval;
	private Random random = new Random();

	public DefaultBotFactory(long minLifeTime, long maxLifeTime, int minMessageInterval, int maxMessageInterval, double messageProbability) {
		this.minLifeTime = minLifeTime;
		this.maxLifeTime = maxLifeTime;
		this.minMessageInterval = minMessageInterval;
		this.maxMessageInterval = maxMessageInterval;
		this.messageProbability = messageProbability;
	}

	@Override
	public ChatBot createBot(SocketAddress serverAddress) throws IOException {
		SocketChatConnection socketChatConnection = new SocketChatConnection();
		socketChatConnection.connect(serverAddress);
		ChatClient<String> chatClient = new ChatClient<>(socketChatConnection, new Utf8StringMessageProtocol(new DefaultProtocol()));
		return new ChatBot(botId.incrementAndGet(), getLifeTime(), minMessageInterval, maxMessageInterval, messageProbability, chatClient);
	}

	private long getLifeTime() {
		return minLifeTime + (long) (random.nextFloat() * (maxLifeTime - minLifeTime));
	}
}
