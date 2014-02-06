package test.chat.client;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static test.chat.client.ChatConnectionListener.CloseListener;
import static test.chat.client.ChatConnectionListener.ConnectListener;
import static test.chat.client.ChatConnectionListener.DataListener;

public class SocketChatConnectionTest {

	private ServerSocket serverSocket;

	@BeforeMethod
	public void setUp() throws Exception {
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
	}

	@AfterMethod
	public void tearDown() throws Exception {
		serverSocket.close();
	}

	@Test
	public void should_call_listeners() throws Exception {
		ConnectListener connectListener = mock(ConnectListener.class);
		CloseListener closeListener = mock(CloseListener.class);
		DataListener dataListener = mock(DataListener.class);

		SocketChatConnection chatConnection = new SocketChatConnection();
		chatConnection.setConnectListener(connectListener);
		chatConnection.setCloseListener(closeListener);
		chatConnection.setDataListener(dataListener);
		chatConnection.connect(serverSocket.getLocalSocketAddress());

		Socket clientSocket = serverSocket.accept();
		chatConnection.update();
		verify(connectListener).onConnect(chatConnection);

		clientSocket.getOutputStream().write(13);
		chatConnection.update();
		verify(dataListener).onData(chatConnection);

		chatConnection.close();
		verify(closeListener).onClose(chatConnection);
	}
}
