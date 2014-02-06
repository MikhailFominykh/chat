package test.chat.server;

import test.chat.ChannelReader;
import test.chat.SelectorProcessor;
import test.chat.util.Updateable;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ClientSocketProcessor implements Updateable {
	private SelectorProcessor selectorProcessor;

	public void init() throws IOException {
		selectorProcessor = new SelectorProcessor(Selector.open());
		selectorProcessor.setReader(createChannelReader());
	}

	public void addClient(Client client, SocketChannel channel) throws IOException {
		channel.configureBlocking(false);
		SelectionKey key = channel.register(selectorProcessor.getSelector(), SelectionKey.OP_READ);
		key.attach(client);
	}

	@Override
	public void update() throws IOException {
		selectorProcessor.update();
	}

	private ChannelReader createChannelReader() {
		return new ChannelReader() {
			@Override
			public void read(SelectionKey key) throws IOException {
				Client client = (Client) key.attachment();
				client.read();
			}
		};
	}
}
