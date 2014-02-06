package test.chat.protocol;

import java.io.IOException;

public interface MessageWriter<T> {
	void write(T s) throws IOException;
}
