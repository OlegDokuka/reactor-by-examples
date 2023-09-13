import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Solution {


	public static void main(String[] args) throws InterruptedException {
		Scheduler scheduler = Schedulers.parallel();

		Scheduler.Worker worker = scheduler.createWorker();

		for (int i = 0; i < 5; i++) {
			var constI = i;
			worker.schedule(() -> System.out.println(Thread.currentThread() + " Hello world " + constI));
		}

//		worker.dispose();

		Thread.sleep(1000);
	}

}
