package test.chat.protocol;

import java.nio.channels.WritableByteChannel;

public interface MessageProtocol<T> {

	public MessageWriter<T> getMessageWriter(WritableByteChannel channel);

	public MessageReader<T> getMessageReader();
}
