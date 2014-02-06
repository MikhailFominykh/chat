package test.chat.client.stress;

import test.chat.util.Runner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class Main {
	private static final String PARAM_SERVER_ADDRESS = "chat.server";
	private static final String PARAM_SERVER_PORT = "chat.port";
	private static final String PARAM_BOT_COUNT = "bot.count";
	private static final String PARAM_THREAD_COUNT = "bot.threads";
	private static final String PARAM_BOT_MIN_LIFE_TIME = "bot.minLifeTime";
	private static final String PARAM_BOT_MAX_LIFE_TIME = "bot.maxLifeTime";
	private static final String PARAM_BOT_MIN_MESSAGE_INTERVAL = "bot.minMessageInterval";
	private static final String PARAM_BOT_MAX_MESSAGE_INTERVAL = "bot.maxMessageInterval";
	private static final String PARAM_BOT_MESSAGE_PROBABILITY = "bot.messageProbability";

	private static final int DEFAULT_BOT_COUNT = 100;
	private static final int DEFAULT_THREAD_COUNT = 1;
	private static final int DEFAULT_BOT_MIN_LIFE_TIME = 60;
	private static final int DEFAULT_BOT_MAX_LIFE_TIME = 240;
	private static final int DEFAULT_BOT_MIN_MESSAGE_INTERVAL = 1;
	private static final int DEFAULT_BOT_MAX_MESSAGE_INTERVAL = 4;
	private static final String DEFAULT_MESSAGE_PROBABILITY = "0.01";

	public static void main(String[] args) throws IOException {
		SocketAddress address = getServerAddress();
		BotFactory botFactory = createBotFactory();

		int botCount = Integer.getInteger(PARAM_BOT_COUNT, DEFAULT_BOT_COUNT);
		int threadCount = Integer.getInteger(PARAM_THREAD_COUNT, DEFAULT_THREAD_COUNT);

		int botsPerThread = botCount / threadCount;
		if (botsPerThread == 0) {
			botsPerThread = 1;
		}

		for (int i = 0; i < threadCount; i++) {
			ChatBotRunner chatBotRunner = new ChatBotRunner(address, botFactory, botsPerThread);
			new Thread(new Runner(chatBotRunner, 10)).start();
		}
	}

	private static BotFactory createBotFactory() {
		int minLifeTime = Integer.getInteger(PARAM_BOT_MIN_LIFE_TIME, DEFAULT_BOT_MIN_LIFE_TIME);
		int maxLifeTime = Integer.getInteger(PARAM_BOT_MAX_LIFE_TIME, DEFAULT_BOT_MAX_LIFE_TIME);
		int minMessageInterval = Integer.getInteger(PARAM_BOT_MIN_MESSAGE_INTERVAL, DEFAULT_BOT_MIN_MESSAGE_INTERVAL);
		int maxMessageInterval = Integer.getInteger(PARAM_BOT_MAX_MESSAGE_INTERVAL, DEFAULT_BOT_MAX_MESSAGE_INTERVAL);
		double messageProbability = Double.parseDouble(System.getProperty(PARAM_BOT_MESSAGE_PROBABILITY, DEFAULT_MESSAGE_PROBABILITY));
		return new DefaultBotFactory(minLifeTime * 1000, maxLifeTime * 1000, minMessageInterval * 1000, maxMessageInterval * 1000, messageProbability);
	}

	private static SocketAddress getServerAddress() throws UnknownHostException {
		InetAddress inetAddress;
		String host = System.getProperty(PARAM_SERVER_ADDRESS);
		if (host != null) {
			inetAddress = InetAddress.getByName(host);
		} else {
			inetAddress = InetAddress.getLocalHost();
		}
		int port = Integer.getInteger(PARAM_SERVER_PORT, 0);
		return new InetSocketAddress(inetAddress, port);
	}
}
