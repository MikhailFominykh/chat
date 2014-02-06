package test.chat.util;

import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class RunnerTest {

	@Test(timeOut = 1000)
	public void should_call_update() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		Updateable updateable = new Updateable() {
			@Override
			public void update() throws Exception {
				latch.countDown();
			}
		};
		Runner runner = new Runner(updateable, 10);
		new Thread(runner).start();

		latch.await();
	}

	@Test(timeOut = 1000)
	public void should_die_when_interrupted() throws Exception {
		Runner runner = new Runner(mock(Updateable.class), 10);
		Thread thread = new Thread(runner);
		thread.start();
		thread.interrupt();
		thread.join();
	}

	@Test(timeOut = 1000)
	public void should_die_when_exception_occurs() throws Exception {
		Updateable updateable = mock(Updateable.class);
		doThrow(new Exception()).when(updateable).update();
		Thread thread = new Thread(new Runner(updateable, 10));
		thread.start();
		thread.join();
	}
}
