package test.chat.client.stress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.client.ChatClient;
import test.chat.client.ChatOutput;
import test.chat.util.CloseableUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

public class ChatBot implements Closeable {
	private static final Logger logger = LogManager.getLogger();

	private int id;
	private double messageProbability;
	private ChatClient<String> chatClient;
	private long closeTime;
	private boolean closed;
	private long nextMessageTime = Long.MAX_VALUE;
	private Random random;
	private int minMessageInterval;
	private int maxMessageInterval;

	public ChatBot(int id, long timeToLive, int minMessageInterval, int maxMessageInterval, double messageProbability, ChatClient<String> chatClient) {
		this.id = id;
		this.minMessageInterval = minMessageInterval;
		this.maxMessageInterval = maxMessageInterval;
		this.messageProbability = messageProbability;
		this.chatClient = chatClient;
		this.closeTime = System.currentTimeMillis() + timeToLive;
		random = new Random();
		chatClient.setOutput(createNameSetter());
	}

	private ChatOutput<String> createNameSetter() {
		return new ChatOutput<String>() {
			@Override
			public void print(String s) {
				logger.trace("Bot {} got server message: {}", id, s);
				chatClient.setOutput(ChatOutput.<String>nullOutput());
				sendBotName();
				setNextMessageTime();
			}
		};
	}

	public void update() throws IOException {
		if (closed) {
			logger.trace("Bot {} already closed", id);
			throw new IOException();
		}
		if (System.currentTimeMillis() > closeTime) {
			close();
		} else {
			sendMessage();
			chatClient.update();
		}
	}

	private void sendMessage() throws IOException {
		if (System.currentTimeMillis() > nextMessageTime) {
			setNextMessageTime();
			if (random.nextDouble() < messageProbability) {
				String message = getMessage();
				logger.trace("Bot {} : {}", id, message);
				chatClient.sendMessage(message);
			}
		}
	}

	private void setNextMessageTime() {
		nextMessageTime = System.currentTimeMillis() + minMessageInterval + random.nextInt(maxMessageInterval - minMessageInterval);
	}

	private String getMessage() {
		return String.valueOf(random.nextLong());
	}

	private void sendBotName() {
		try {
			chatClient.sendMessage("Bot_" + id);
		} catch (IOException e) {
			logger.catching(e);
			CloseableUtils.closeSilently(this);
		}
	}

	@Override
	public void close() throws IOException {
		if (!closed) {
			closed = true;
			chatClient.close();
		}
	}

	public int getId() {
		return id;
	}
}
