package test.chat.protocol;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class DefaultPacketWriterTest {

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void should_throw_illegal_argument_exception_when_data_is_null() throws Exception {
		DefaultPacketWriter packetWriter = new DefaultPacketWriter(mock(WritableByteChannel.class), 1);
		packetWriter.write(null);
	}

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void should_throw_illegal_argument_exception_when_packet_size_is_zero() throws Exception {
		new DefaultPacketWriter(mock(WritableByteChannel.class), 0);
	}

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void should_throw_illegal_argument_exception_when_packet_size_is_less_than_zero() throws Exception {
		new DefaultPacketWriter(mock(WritableByteChannel.class), -1);
	}

	@Test
	public void should_correctly_write_packet() throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		WritableByteChannel channel = Channels.newChannel(outputStream);
		DefaultPacketWriter packetWriter = new DefaultPacketWriter(channel, 100);

		byte[] data = new byte[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
		packetWriter.write(data);

		ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());

		assertEquals(buffer.getShort(), data.length);
		byte[] bytes = new byte[10];
		buffer.get(bytes);
		assertEquals(bytes, data);
	}

	@Test(expectedExceptions = {IOException.class})
	public void should_throw_exception_when_channel_throws_exception() throws Exception {
		WritableByteChannel channel = mock(WritableByteChannel.class);
		when(channel.write(any(ByteBuffer.class))).thenThrow(new IOException());
		DefaultPacketWriter packetWriter = new DefaultPacketWriter(channel, 100);

		byte[] data = new byte[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
		packetWriter.write(data);
	}
}
