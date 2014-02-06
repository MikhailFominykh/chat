package test.chat.server;

import org.mockito.InOrder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.chat.protocol.MessageProtocol;
import test.chat.protocol.MessageReader;
import test.chat.protocol.MessageWriter;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientTest {

	MessageProtocol protocol;
	MessageReader reader;
	MessageWriter writer;

	@BeforeMethod
	public void setUp() throws Exception {
		protocol = mock(MessageProtocol.class);
		reader = mock(MessageReader.class);
		writer = mock(MessageWriter.class);
		when(protocol.getMessageReader()).thenReturn(reader);
		when(protocol.getMessageWriter(any(WritableByteChannel.class))).thenReturn(writer);
	}

	@Test
	public void should_ask_for_data() throws Exception {
		ByteChannel channel = mock(ByteChannel.class);
		when(channel.read(any(ByteBuffer.class))).thenReturn(0);
		Client client = new Client(channel, protocol);
		client.read();
		verify(reader).read(channel);
	}

	@Test
	public void should_send_messages_to_processor_in_correct_order() throws Exception {
		when(reader.read(any(ReadableByteChannel.class))).thenReturn(Arrays.asList("one", "two", "three"));

		MessageProcessor messageProcessor = mock(MessageProcessor.class);
		InOrder inOrder = inOrder(messageProcessor);

		Client client = new Client(mock(ByteChannel.class), protocol);
		client.setMessageProcessor(messageProcessor);
		client.read();

		inOrder.verify(messageProcessor, times(1)).processMessage(client, "one");
		inOrder.verify(messageProcessor, times(1)).processMessage(client, "two");
		inOrder.verify(messageProcessor, times(1)).processMessage(client, "three");
	}

	@Test
	public void should_write_data() throws Exception {
		Client client = new Client(mock(ByteChannel.class), protocol);
		InOrder inOrder = inOrder(writer);
		client.send("one");
		client.send("two");
		client.send("three");
		inOrder.verify(writer).write("one");
		inOrder.verify(writer).write("two");
		inOrder.verify(writer).write("three");
	}

	@Test
	public void should_set_name() throws Exception {
		String name = "Test name";
		Client client = new Client(mock(ByteChannel.class), protocol);
		client.setName("Test name");
		Assert.assertEquals(client.getName(), name);
	}
}
