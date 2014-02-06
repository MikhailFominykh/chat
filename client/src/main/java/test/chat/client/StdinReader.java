package test.chat.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StdinReader implements Runnable {
	private static final Logger logger = LogManager.getLogger();

	private ChatClient<String> chatClient;

	public StdinReader(ChatClient<String> chatClient) {
		this.chatClient = chatClient;
	}

	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			while (!Thread.interrupted() && (line = reader.readLine()) != null) {
				chatClient.sendMessage(line);
			}
		} catch (IOException e) {
			logger.catching(e);
		}
	}
}
