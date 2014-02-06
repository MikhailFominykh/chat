package test.chat.client;

import java.io.PrintStream;

public class PrintStreamOutput extends ChatOutput<String> {
	private PrintStream stream;

	public PrintStreamOutput(PrintStream stream) {
		this.stream = stream;
	}

	@Override
	public void print(String s) {
		stream.println(s);
	}
}
