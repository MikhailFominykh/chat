package test.chat.client.stress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.chat.util.Updateable;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChatBotRunner implements Updateable {
	private static final Logger logger = LogManager.getLogger();

	private BotFactory botFactory;
	private int maxBotCount;
	private List<ChatBot> bots = new LinkedList<>();
	private SocketAddress serverAddress;

	public ChatBotRunner(SocketAddress serverAddress, BotFactory botFactory, int maxBotCount) {
		this.serverAddress = serverAddress;
		this.botFactory = botFactory;
		this.maxBotCount = maxBotCount;
	}

	@Override
	public void update() throws Exception {
		addNewBots();
		updateBots();
	}

	private void addNewBots() {
		if (bots.size() < maxBotCount) {
			createBot();
		}
	}

	private void createBot() {
		try {
			ChatBot bot = botFactory.createBot(serverAddress);
			bots.add(bot);
		} catch (IOException e) {
			logger.catching(e);
		}
	}

	private void updateBots() {
		Iterator<ChatBot> it = bots.iterator();
		while (it.hasNext()) {
			ChatBot bot = it.next();
			try {
				bot.update();
			} catch (IOException e) {
				it.remove();
				logger.debug("Bot removed {}", bot.getId());
			}
		}
	}
}
