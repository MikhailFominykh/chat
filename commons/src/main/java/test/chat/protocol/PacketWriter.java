package test.chat.protocol;

import java.io.IOException;

public interface PacketWriter {
	void write(byte[] bytes) throws IOException;
}
