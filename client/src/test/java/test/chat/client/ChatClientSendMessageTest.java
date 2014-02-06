package test.chat.client;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.chat.protocol.MessageProtocol;
import test.chat.protocol.MessageReader;
import test.chat.protocol.MessageWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.List;

public class ChatClientSendMessageTest {
	FakeChatConnection connection;
	MessageProtocol<String> protocol;
	ByteArrayOutputStream outputStream;
	List<String> messages;
	byte[] expectedBytes;

	@BeforeMethod
	public void setUp() throws Exception {
		connection = new FakeChatConnection(false);
		outputStream = new ByteArrayOutputStream();
		connection.setOutputChannel(Channels.newChannel(outputStream));

		protocol = new MessageProtocol<String>() {
			@Override
			public MessageWriter<String> getMessageWriter(WritableByteChannel channel) {
				final WritableByteChannel ch = channel;
				return new MessageWriter<String>() {
					@Override
					public void write(String s) throws IOException {
						ch.write(ByteBuffer.wrap(s.getBytes()));
					}
				};
			}

			@Override
			public MessageReader<String> getMessageReader() {
				return null;
			}
		};

		messages = Arrays.asList("one", "two", "three");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		for (String message : messages) {
			os.write(message.getBytes());
		}
		expectedBytes = os.toByteArray();
	}

	@Test
	public void should_send_messages_to_opened_connection() throws Exception {
		connection.onConnect();

		ChatClient<String> chatClient = new ChatClient<>(connection, protocol);
		for (String message : messages) {
			chatClient.sendMessage(message);
		}

		chatClient.update();

		Assert.assertEquals(outputStream.toByteArray(), expectedBytes);
	}

	@Test
	public void should_send_messages_after_connection_has_been_opened() throws Exception {
		ChatClient<String> chatClient = new ChatClient<>(connection, protocol);
		for (String message : messages) {
			chatClient.sendMessage(message);
		}

		connection.onConnect();

		chatClient.update();

		Assert.assertEquals(outputStream.toByteArray(), expectedBytes);
	}
}
