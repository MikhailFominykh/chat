package test.chat.protocol;

import java.nio.channels.WritableByteChannel;

public interface Protocol {
	public PacketReader getPacketReader();

	public PacketWriter getPacketWriter(WritableByteChannel channel);
}
