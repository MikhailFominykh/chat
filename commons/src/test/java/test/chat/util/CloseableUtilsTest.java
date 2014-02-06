package test.chat.util;

import org.testng.annotations.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CloseableUtilsTest {

	@Test
	public void should_call_close() throws Exception {
		Closeable closeable = mock(Closeable.class);
		CloseableUtils.closeSilently(closeable);
		verify(closeable, times(1)).close();
	}

	@Test
	public void should_not_throw_exception() throws Exception {
		Closeable closeable = mock(Closeable.class);
		doThrow(new IOException()).when(closeable).close();
		CloseableUtils.closeSilently(closeable);
	}
}
