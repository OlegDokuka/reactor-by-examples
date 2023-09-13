import io.socket.client.IO;
import io.socket.client.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Problem {
	static final Logger logger =
			LoggerFactory.getLogger(Problem.class);


	public static void main(String[] mainArgs) throws InterruptedException {
		Sinks.Many<Object> objectMany = Sinks.unsafe()
				.many()
				.unicast()
				.onBackpressureBuffer(Queues.unboundedMultiproducer().get());

		objectMany.asFlux().subscribe(null, null, null, s -> {});
		objectMany.asFlux().subscribe();


		objectMany.tryEmitNext("hello_world");


	}


	static void listenRemote(Consumer<String> event, CountDownLatch latch) {

		Socket socket;
		try {
			socket = IO.socket("https://streamer.cryptocompare.com");
			logger.info("[EXTERNAL-SERVICE] Connecting to CryptoCompare.com ...");
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		socket.on(Socket.EVENT_CONNECT, args -> {
					String[] subscription =
							{"5~CCCAGG~BTC~USD", "0~Coinbase~BTC~USD", "0~Cexio~BTC~USD"};
					Map<String, Object> subs = new HashMap<>();
					subs.put("subs", subscription);
					socket.emit("SubAdd", subs);

					logger.info("[EXTERNAL-SERVICE] Connected to CryptoCompare.com ...");
				})
				.on("m", args -> {
					String message = args[0].toString();
					event.accept(message);
					logger.info("Handled message {}", message);
				})
				.on(Socket.EVENT_ERROR, args -> {
					// TODO: handle error
					logger.error("Handled error {}", args);
					latch.countDown();
				})
				.on(Socket.EVENT_DISCONNECT, args -> {
					// TODO: handle disconnection
					logger.error("Handled disconnection {}", args);
					latch.countDown();
				});



		// TODO: how???
		Runnable closeSocket = () -> {
			socket.close();
			logger.info("[EXTERNAL-SERVICE] Connection to CryptoCompare.com closed");
		};

		socket.connect();
	}
}
