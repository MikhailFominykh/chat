package test.chat.protocol;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class DefaultPacketReaderTest {

	@Test(expectedExceptions = {IOException.class})
	public void should_throw_exception_when_data_size_is_zero() throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{0, 0});
		ReadableByteChannel channel = Channels.newChannel(inputStream);

		PacketReader packetReader = new DefaultPacketReader(10);
		packetReader.read(channel);
	}

	@Test
	public void should_read_data() throws Exception {
		List<byte[]> data = Arrays.asList(new byte[]{1, 2, 3}, new byte[]{4, 5, 6, 7});

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		WritableByteChannel channel = Channels.newChannel(outputStream);
		ByteBuffer sizeBuffer = ByteBuffer.allocate(2);
		for (byte[] bytes : data) {
			sizeBuffer.clear();
			sizeBuffer.putShort((short) bytes.length);
			sizeBuffer.flip();
			channel.write(sizeBuffer);
			channel.write(ByteBuffer.wrap(bytes));
		}

		final byte[] bytes = outputStream.toByteArray();

		ReadableByteChannel readableByteChannel = new ReadableByteChannel() {
			boolean empty = false;

			@Override
			public int read(ByteBuffer dst) throws IOException {
				if (!empty) {
					dst.put(bytes);
					empty = true;
					return bytes.length;
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
		};
		DefaultPacketReader reader = new DefaultPacketReader(100);
		List<byte[]> read = reader.read(readableByteChannel);

		assertEquals(read, data);
	}
}
