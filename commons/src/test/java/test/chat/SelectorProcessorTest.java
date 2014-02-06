package test.chat;

import com.google.common.collect.Sets;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectorProcessorTest {
	private static final boolean VALID = true;
	private static final boolean INVALID = false;

	Selector selector;
	ChannelAcceptor acceptor;
	ChannelConnector connector;
	ChannelReader reader;
	private SelectorProcessor processor;

	@BeforeMethod
	public void setUp() throws Exception {
		selector = mock(Selector.class);
		acceptor = mock(ChannelAcceptor.class);
		connector = mock(ChannelConnector.class);
		reader = mock(ChannelReader.class);

		processor = new SelectorProcessor(selector);
		processor.setAcceptor(acceptor);
		processor.setConnector(connector);
		processor.setReader(reader);
	}

	private SelectionKey createSelectionKey(boolean valid, int readyOps) {
		SelectionKey selectionKey = mock(SelectionKey.class);
		when(selectionKey.isValid()).thenReturn(valid);
		when(selectionKey.readyOps()).thenReturn(readyOps);
		return selectionKey;
	}

	private void configureSelectedKeys(SelectionKey... keys) throws IOException {
		when(selector.selectNow()).thenReturn(keys.length);
		when(selector.selectedKeys()).thenReturn(Sets.newHashSet(keys));
	}

	@Test
	public void should_call_acceptor() throws Exception {
		SelectionKey selectionKey1 = createSelectionKey(VALID, SelectionKey.OP_ACCEPT);
		SelectionKey selectionKey2 = createSelectionKey(VALID, SelectionKey.OP_ACCEPT);
		configureSelectedKeys(selectionKey1, selectionKey2);

		processor.update();

		verify(acceptor).accept(selectionKey1);
		verify(acceptor).accept(selectionKey2);
	}

	@Test
	public void should_call_connector() throws Exception {
		SelectionKey selectionKey1 = createSelectionKey(VALID, SelectionKey.OP_CONNECT);
		SelectionKey selectionKey2 = createSelectionKey(VALID, SelectionKey.OP_CONNECT);
		configureSelectedKeys(selectionKey1, selectionKey2);

		processor.update();

		verify(connector).connect(selectionKey1);
		verify(connector).connect(selectionKey2);
	}

	@Test
	public void should_call_reader() throws Exception {
		SelectionKey selectionKey1 = createSelectionKey(VALID, SelectionKey.OP_READ);
		SelectionKey selectionKey2 = createSelectionKey(VALID, SelectionKey.OP_READ);
		configureSelectedKeys(selectionKey1, selectionKey2);

		processor.update();

		verify(reader).read(selectionKey1);
		verify(reader).read(selectionKey2);
	}

	@Test
	public void should_not_call_listeners_for_invalid_keys() throws Exception {
		SelectionKey validKey = createSelectionKey(VALID, SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
		SelectionKey invalidKey = createSelectionKey(INVALID, SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
		configureSelectedKeys(validKey, invalidKey);

		processor.update();

		verify(acceptor).accept(validKey);
		verify(connector).connect(validKey);
		verify(reader).read(validKey);

		verify(acceptor, never()).accept(invalidKey);
		verify(connector, never()).connect(invalidKey);
		verify(reader, never()).read(invalidKey);
	}

	@Test
	public void should_return_selector() throws Exception {
		assertEquals(processor.getSelector(), selector);
	}

	@Test
	public void should_close_selector() throws Exception {
		processor.close();
		verify(selector, times(1)).close();
	}
}
