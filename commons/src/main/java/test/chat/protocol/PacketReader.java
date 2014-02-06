package test.chat.protocol;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

public interface PacketReader {
	List<byte[]> read(ReadableByteChannel channel) throws IOException;
}
