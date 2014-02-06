package test.chat.client;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import static test.chat.client.ChatConnectionListener.CloseListener;
import static test.chat.client.ChatConnectionListener.ConnectListener;
import static test.chat.client.ChatConnectionListener.DataListener;

public class FakeChatConnection implements ChatConnection {
	private boolean connected;
	private DataListener dataListener;
	private CloseListener closeListener;
	private ConnectListener connectListener;
	private WritableByteChannel outputChannel;
	private ReadableByteChannel inputChannel;

	public FakeChatConnection(boolean connected) {
		this.connected = connected;
	}

	@Override
	public WritableByteChannel getOutputChannel() throws IOException {
		return outputChannel;
	}

	@Override
	public ReadableByteChannel getInputChannel() throws IOException {
		return inputChannel;
	}

	@Override
	public void setConnectListener(ConnectListener connectListener) {
		this.connectListener = connectListener;
	}

	@Override
	public void setCloseListener(CloseListener closeListener) {
		this.closeListener = closeListener;
	}

	@Override
	public void setDataListener(DataListener dataListener) {
		this.dataListener = dataListener;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void update() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	public void onConnect() {
		connected = true;
		if (connectListener != null) {
			connectListener.onConnect(this);
		}
	}

	public void onData() {
		if (dataListener != null) {
			dataListener.onData(this);
		}
	}

	public void setOutputChannel(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}

	public void setInputChannel(ReadableByteChannel inputChannel) {
		this.inputChannel = inputChannel;
	}
}
