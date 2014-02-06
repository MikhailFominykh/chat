package test.chat.client;

import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.chat.protocol.MessageProtocol;
import test.chat.protocol.MessageReader;
import test.chat.protocol.MessageWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class ChatClientReceiveMessageTest {
	List<String> messages;
	MessageProtocol protocol;
	FakeChatConnection connection;

	@BeforeMethod
	public void setUp() throws Exception {
		messages = Arrays.asList("one", "two", "three");
		connection = new FakeChatConnection(true);
		byte[] bytes = new byte[messages.size()];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) i;
		}
		connection.setInputChannel(new FakeReadableByteChannel(bytes));
		protocol = new MessageProtocol<String>() {
			@Override
			public MessageWriter<String> getMessageWriter(WritableByteChannel channel) {
				return null;
			}

			@Override
			public MessageReader<String> getMessageReader() {
				return new MessageReader<String>() {
					@Override
					public List<String> read(ReadableByteChannel channel) throws IOException {
						ByteBuffer buffer = ByteBuffer.allocate(10);
						channel.read(buffer);
						buffer.flip();
						List<String> result = new ArrayList<>();
						while (buffer.remaining() > 0) {
							result.add(messages.get(buffer.get()));
						}
						return result;
					}
				};
			}
		};
	}

	@Test
	public void should_receive_messages() throws Exception {
		ChatOutput<String> output = mock(ChatOutput.class);
		InOrder inOrder = inOrder(output);

		ChatClient<String> chatClient = new ChatClient<String>(connection, protocol);
		chatClient.setOutput(output);
		chatClient.update();
		connection.onData();

		for (String message : messages) {
			inOrder.verify(output).print(message);
		}
	}
}
