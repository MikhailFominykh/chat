package test.chat.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;

public class StringMessageWriter implements MessageWriter<String> {
	private CharsetEncoder encoder;
	private PacketWriter packetWriter;

	public StringMessageWriter(CharsetEncoder encoder, PacketWriter packetWriter) {
		this.encoder = encoder;
		this.packetWriter = packetWriter;
	}

	@Override
	public void write(String s) throws IOException {
		ByteBuffer buffer = encoder.encode(CharBuffer.wrap(s));
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		packetWriter.write(bytes);
	}
}
