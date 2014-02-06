package test.chat.protocol;

import com.google.common.base.Charsets;

import java.nio.channels.WritableByteChannel;

public class Utf8StringMessageProtocol implements MessageProtocol<String> {
	private Protocol protocol;

	public Utf8StringMessageProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public MessageWriter<String> getMessageWriter(WritableByteChannel channel) {
		return new StringMessageWriter(Charsets.UTF_8.newEncoder(), protocol.getPacketWriter(channel));
	}

	@Override
	public MessageReader<String> getMessageReader() {
		return new StringMessageReader(Charsets.UTF_8.newDecoder(), protocol.getPacketReader());
	}
}
