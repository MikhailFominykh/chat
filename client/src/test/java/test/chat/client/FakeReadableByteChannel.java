package test.chat.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class FakeReadableByteChannel implements ReadableByteChannel {
	private byte[] data;
	private boolean hasData = true;

	public FakeReadableByteChannel(byte[] data) {
		this.data = data;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		if (hasData) {
			hasData = false;
			dst.put(data);
			return data.length;
		}
		return 0;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void close() throws IOException {
	}
}
