package test.chat.client;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import static test.chat.client.ChatConnectionListener.CloseListener;
import static test.chat.client.ChatConnectionListener.ConnectListener;
import static test.chat.client.ChatConnectionListener.DataListener;

public interface ChatConnection extends Closeable {

	public WritableByteChannel getOutputChannel() throws IOException;

	public ReadableByteChannel getInputChannel() throws IOException;

	public void setConnectListener(ConnectListener connectListener);

	public void setCloseListener(CloseListener closeListener);

	public void setDataListener(DataListener dataListener);

	public boolean isConnected();

	public void update() throws IOException;
}
