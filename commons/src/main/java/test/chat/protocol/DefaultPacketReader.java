package test.chat.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultPacketReader implements PacketReader {
	private ByteBuffer buffer;

	private int dataSize;
	private List<byte[]> messages = new ArrayList<>();

	protected DefaultPacketReader(int maxPacketSize) {
		if (maxPacketSize <= 0) {
			throw new IllegalArgumentException("Wrong maxPacketSize: " + maxPacketSize);
		}
		this.buffer = ByteBuffer.allocate(maxPacketSize);
	}

	@Override
	public List<byte[]> read(ReadableByteChannel channel) throws IOException {
		while (true) {
			int r = channel.read(buffer);
			if (r == -1) {
				throw new IOException("EOF");
			}
			if (r == 0) {
				break;
			}
			buffer.flip();
			while (dataSize <= buffer.remaining()) {
				if (dataSize == 0) {
					if (buffer.remaining() > 1) {
						readDataSize();
					} else {
						break;
					}
				} else {
					readData();
				}
			}
			buffer.compact();
		}
		return getResult();
	}

	private void readDataSize() throws IOException {
		assert buffer.remaining() > 1;

		dataSize = buffer.getShort();
		if (dataSize <= 0 || dataSize > buffer.capacity()) {
			throw new IOException("Wrong data size");
		}
	}

	private void readData() {
		assert dataSize <= buffer.remaining();

		byte[] message = new byte[dataSize];
		buffer.get(message);
		dataSize = 0;

		messages.add(message);
	}

	private List<byte[]> getResult() {
		if (messages.size() > 0) {
			ArrayList<byte[]> result = new ArrayList<>(messages);
			messages.clear();
			return result;
		}
		return Collections.emptyList();
	}
}
