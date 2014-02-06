package test.chat.protocol;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

public interface MessageReader<T> {
	List<T> read(ReadableByteChannel channel) throws IOException;
}
