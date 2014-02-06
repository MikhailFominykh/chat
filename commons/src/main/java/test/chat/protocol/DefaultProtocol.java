package test.chat.protocol;

import java.nio.channels.WritableByteChannel;

public class DefaultProtocol implements Protocol {
	public static final int DEFAULT_PACKET_SIZE = 1024;

	private int maxPacketSize;

	public DefaultProtocol() {
		this(DEFAULT_PACKET_SIZE);
	}

	public DefaultProtocol(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

	@Override
	public PacketReader getPacketReader() {
		return new DefaultPacketReader(maxPacketSize);
	}

	@Override
	public PacketWriter getPacketWriter(WritableByteChannel channel) {
		return new DefaultPacketWriter(channel, maxPacketSize);
	}
}
