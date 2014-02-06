package test.chat.protocol;

import com.google.common.base.Charsets;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StringMessageWriterTest {

	@Test
	public void should_write_encoded_string() throws Exception {
		CharsetEncoder encoder = Charsets.UTF_8.newEncoder();
		PacketWriter packetWriter = mock(PacketWriter.class);

		String s = "Test string";
		ByteBuffer buffer = encoder.encode(CharBuffer.wrap(s));
		byte[] encodedBytes = new byte[buffer.remaining()];
		buffer.get(encodedBytes);
		MessageWriter stringWriter = new StringMessageWriter(encoder, packetWriter);
		stringWriter.write(s);

		verify(packetWriter).write(eq(encodedBytes));
	}

	@Test(expectedExceptions = {IOException.class})
	public void should_rethrow_packet_writer_exception() throws Exception {
		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
		PacketWriter packetWriter = mock(PacketWriter.class);
		doThrow(new IOException()).when(packetWriter).write(any(byte[].class));

		MessageWriter stringWriter = new StringMessageWriter(encoder, packetWriter);
		stringWriter.write("Test");
	}
}
