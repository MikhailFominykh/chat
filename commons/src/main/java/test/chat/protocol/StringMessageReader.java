package test.chat.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringMessageReader implements MessageReader<String> {
	private CharsetDecoder charsetDecoder;
	private PacketReader packetReader;

	public StringMessageReader(CharsetDecoder charsetDecoder, PacketReader packetReader) {
		this.charsetDecoder = charsetDecoder;
		this.packetReader = packetReader;
	}

	@Override
	public List<String> read(ReadableByteChannel channel) throws IOException {
		List<byte[]> messages = packetReader.read(channel);
		if (messages.size() == 0) {
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<>();
		for (byte[] message : messages) {
			result.add(decode(message));
		}
		return result;
	}

	private String decode(byte[] message) throws CharacterCodingException {
		return charsetDecoder.decode(ByteBuffer.wrap(message)).toString();
	}
}
