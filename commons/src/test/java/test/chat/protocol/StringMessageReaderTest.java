package test.chat.protocol;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class StringMessageReaderTest {
	Function<String, byte[]> utf8Encoder = new Function<String, byte[]>() {
		@Override
		public byte[] apply(String s) {
			try {
				ByteBuffer buffer = Charsets.UTF_8.newEncoder().encode(CharBuffer.wrap(s));
				byte[] bytes = new byte[buffer.remaining()];
				buffer.get(bytes);
				return bytes;
			} catch (CharacterCodingException e) {
				return new byte[]{};
			}
		}
	};

	@Test
	public void should_read_strings() throws Exception {
		List<String> originalStrings = Arrays.asList("123", "sdjkfh dsafhasd  fdsjah asd dsajhfjh  sdajfhdsj ksdhfkjasd");
		List<byte[]> data = new ArrayList<>(Collections2.transform(originalStrings, utf8Encoder));

		PacketReader packetReader = mock(PacketReader.class);
		when(packetReader.read(any(ReadableByteChannel.class))).thenReturn(data);

		CharsetDecoder charsetDecoder = Charsets.UTF_8.newDecoder();

		MessageReader<String> reader = new StringMessageReader(charsetDecoder, packetReader);
		List<String> strings = reader.read(mock(ReadableByteChannel.class));

		assertEquals(strings, originalStrings);
	}

	@Test(expectedExceptions = {IOException.class})
	public void should_throw_exception() throws Exception {
		PacketReader packetReader = mock(PacketReader.class);
		when(packetReader.read(any(ReadableByteChannel.class))).thenThrow(new IOException());

		CharsetDecoder charsetDecoder = Charsets.UTF_8.newDecoder();

		MessageReader<String> reader = new StringMessageReader(charsetDecoder, packetReader);
		reader.read(mock(ReadableByteChannel.class));
	}
}
