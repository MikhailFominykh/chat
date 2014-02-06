package test.chat;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class SelectorProcessor implements Closeable {
	private Selector selector;
	private ChannelAcceptor acceptor = ChannelAcceptor.NULL;
	private ChannelConnector connector = ChannelConnector.NULL;
	private ChannelReader reader = ChannelReader.NULL;

	public SelectorProcessor(Selector selector) {
		if (selector == null) {
			throw new IllegalArgumentException("Parameter 'selector' is null");
		}
		this.selector = selector;
	}

	@Override
	public void close() throws IOException {
		selector.close();
	}

	public Selector getSelector() {
		return selector;
	}

	public void setAcceptor(ChannelAcceptor acceptor) {
		if (acceptor == null) {
			throw new IllegalArgumentException("Parameter 'acceptor' is null");
		}
		this.acceptor = acceptor;
	}

	public void setConnector(ChannelConnector connector) {
		if (connector == null) {
			throw new IllegalArgumentException("Parameter 'connector' is null");
		}
		this.connector = connector;
	}

	public void setReader(ChannelReader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("Parameter 'reader' is null");
		}
		this.reader = reader;
	}

	public void update() throws IOException {
		int keyCount = selector.selectNow();
		if (keyCount > 0) {
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				if (key.isValid() && key.isAcceptable()) {
					acceptor.accept(key);
				}
				if (key.isValid() && key.isConnectable()) {
					connector.connect(key);
				}
				if (key.isValid() && key.isReadable()) {
					reader.read(key);
				}
			}
		}
	}
}
