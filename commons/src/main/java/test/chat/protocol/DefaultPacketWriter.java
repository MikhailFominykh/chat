package test.chat.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class DefaultPacketWriter implements PacketWriter {
	private WritableByteChannel channel;
	private ByteBuffer buffer;

	protected DefaultPacketWriter(WritableByteChannel channel, int maxPacketSize) {
		if (channel == null) {
			throw new IllegalArgumentException("Parameter 'channel' is null");
		}
		if (maxPacketSize <= 0) {
			throw new IllegalArgumentException("Wrong max packet size: " + maxPacketSize);
		}
		this.channel = channel;
		this.buffer = ByteBuffer.allocate(maxPacketSize);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		if (bytes == null) {
			throw new IllegalArgumentException("Parameter 'bytes' is null");
		}
		if (bytes.length == 0) {
			return;
		}
		if (bytes.length > buffer.capacity() - 2) {
			throw new IllegalArgumentException("Data size is too big");
		}
		buffer.clear();
		buffer.putShort((short) bytes.length);
		buffer.put(bytes);
		buffer.flip();
		while (buffer.remaining() > 0) {
			channel.write(buffer);
		}
	}
}
